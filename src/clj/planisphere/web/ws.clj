(ns planisphere.web.ws
  (:require [compojure.core :as comp :refer [defroutes GET POST]]
            [taoensso.sente :refer [make-channel-socket-server! debug-mode?_]]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [taoensso.sente.packers.transit :refer [get-transit-packer]]
            [taoensso.timbre :refer [infof debugf set-level!]]
            [clojure.core.async :as async :refer [<! <!! >! pipe chan go sub pub]]
            [cognitect.transit :as transit]
            [ring.util.http-response :as response]))

;;(reset! debug-mode?_ true)

(defn transit->clj
  [obj]
  (transit/read (transit/reader obj :json)))

(defn clj->transit
  [obj]
  (let [output (java.io.ByteArrayOutputStream.)]
    (transit/write (transit/writer output :json) obj)
    (java.nio.ByteBuffer/wrap (.toByteArray output))))

(let [packer (get-transit-packer)
      connection (make-channel-socket-server! (get-sch-adapter)
                                                    {:packer packer})]
  (def ring-ajax-post (:ajax-post-fn connection))
  (def ring-ajax-get-or-ws-handshake (:ajax-get-or-ws-handshake-fn connection))
  (def ch-chsk (:ch-recv connection))
  (def chsk-send! (:send-fn connection))
  (def connected-uids (add-watch (:connected-uids connection) :connected-uids
                                 (fn [_ _ old new]
                                   (when (not= old new)
                                     (debugf "Connected uids change: %s" new))))))

(def rendered-xf (map (comp response/ok :?data)))
(def render-fail-xf (map (comp response/unprocessable-entity :?data)))
(def response-xf (map #(update % :body clj->transit)))

(def sente-pub (pub ch-chsk :id))
(def rendered-chan (sub sente-pub :planisphere/rendered (chan 1 rendered-xf)))
(def render-fail-chan (sub sente-pub :planisphere/render-error (chan 1 render-fail-xf)))
(def response-chan (chan 1 response-xf))
(pipe (async/merge [rendered-chan render-fail-chan] 1) response-chan)

(defn broadcast-log-level [level]
  (if-let [uids (seq (:any @connected-uids))]
    (doseq [uid uids]
      (chsk-send! uid [:planisphere/log-level level]))))

(defn broadcast-spec [req]
  (go
    (if-let [uids (seq (:any @connected-uids))]
      (doseq [uid uids]
        (chsk-send! uid [:planisphere/spec (transit->clj (:body req))]))
      (>! response-chan (response/precondition-failed "No client browser found")))
    (<! (async/take 1 response-chan))))

(defn spec-render-handler
  [req]
  (<!! (broadcast-spec req)))

(defroutes websocket-routes
  (POST "/spec" [] spec-render-handler)
  (GET  "/ws" req (ring-ajax-get-or-ws-handshake req))
  (POST "/ws" req (ring-ajax-post req)))
