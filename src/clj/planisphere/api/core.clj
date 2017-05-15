(ns planisphere.api.core
  (:require
   [mount.core :as mount :refer [defstate]]
   [cprop.core :refer [load-config]]
   [planisphere.api.validation :refer [valid?]]
   [planisphere.web.server :refer [web-server
                                   set-client-level!]]
   [planisphere.web.browser :refer [url
                                    default-browser
                                    headless-browser]]
   [clj-http.client :as http]
   [analemma.xml :refer [parse-xml]]
   [tikkba.dom :refer [svg-doc]]
   [tikkba.transcoder :refer [to-jpeg]]
   [taoensso.timbre :as timbre :refer [set-level! info]]
   [ring.util.http-predicates :refer [success?]]
   [ring.util.http-response :refer [throw!]]))

(defstate ^:private cookie-store :start (clj-http.cookies/cookie-store))

(defstate ^:private anti-forgery-token :start (-> (http/get (str (url (mount/args)) "token")
                                                            {:cookie-store cookie-store
                                                             :as :transit+json})
                                                  :body
                                                  :csrf-token))

(defn stop-app
  "Terminate the client session and the application server."
  []
  (let [{:keys [stopped]} (mount/stop)]
    (timbre/infof "Stopped: %s" stopped)))

(defn start-app
  "Start the application server and connect a client browser.
   Client can be configured as headless for server-side execution."
  [user-opts]
  (let [opts (load-config :merge [user-opts])]
    (set-level! (:log-level opts))
    (let [{:keys [started]}
          (->
           (if (:headless? opts)
             (mount/except #{#'default-browser})
             (mount/except #{#'headless-browser}))
           (mount/only #{#'valid?
                         #'default-browser
                         #'headless-browser
                         #'web-server
                         #'cookie-store
                         #'anti-forgery-token})
           (mount/with-args opts)
           mount/start)]
      (set-client-level! (:log-level opts))
      (timbre/infof "Started: %s" started)))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn validate-spec
  "Validate a Vega or Vega-Lite plot :spec.
  :spec can be given as a Clojure map or JSON string.
  This function can be used outside of the start-app workflow."
  [m & {:as schemas}]
  ;; Load schemas if they haven't already been loaded by start-app
  (when-not (fn? valid?)
    (-> (mount/with-args (load-config :merge [(or schemas {})]))
        (mount/only [#'valid?])
        mount/start)
    (validate-spec m))
  ;; Validate after schemas are loaded
  (when-let [[err] (valid? m)]
    (slingshot.slingshot/throw+ err)))

(defn send-spec
  "Send a :spec of :type :vega or :vega-lite to the client browser.
  Given map should also contain a user-specified :id .
  :spec will be validated against the corresponding Vega/Vega-Lite schema
  before being sent for rendering."
  [m & {:keys [return] :or {return [:id :svg]}}]
  (timbre/debugf "Spec validation complete. Sending request with form-params: %s" m)
  (let [form-params (assoc m :return return)
        response (http/post (str (url (mount/args)) "spec")
                            {:cookie-store cookie-store
                             :headers {"X-CSRF-Token" anti-forgery-token}
                             :throw-exceptions false
                             :form-params form-params
                             :content-type :transit+json
                             :as :transit+json})]
    (if-not (success? response)
      (throw! response)
      (:body response))))

(defn svg->jpeg
  "Render an svg string to a jpeg file."
  [filename svg]
  (to-jpeg (-> svg parse-xml svg-doc) filename))
