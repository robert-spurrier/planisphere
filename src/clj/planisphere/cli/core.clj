(ns planisphere.cli.core
  (:require
   [planisphere.api.core :as planisphere]
   [cheshire.core :refer [parse-string]]
   [clojure.string :as string]
   [clojure.tools.cli :refer [parse-opts]]
   [clojure.java.io :as io]
   [taoensso.timbre :as timbre :refer [info infof errorf debugf]]
   [ring.util.http-predicates :refer [precondition-failed?]]
   [slingshot.slingshot :refer [try+ throw+ get-thrown-object]]
   [diehard.core :as dh])
  (:gen-class))

(def json-file-xf
  (comp
   (filter #(.isFile %))
   (filter #(.endsWith (.getName %) ".json"))))

(defn json-files
  [f]
  (into [] json-file-xf (file-seq f)))

(def cli-options
  [["-H" "--host HOST" "Server host"
    :default "localhost"]
   ["-p" "--port PORT" "Port number"
    :default 10666
    :parse-fn #(Integer/parseInt %)
    :validate [#(<= 0 % 0x10000) "Must be a number between 0 and 65536"]]
   [nil "--headless" "Use the headless browser client"
    :id :headless?]
   ["-l" "--log-level LEVEL" "Server & client logging level"
    :parse-fn #(keyword (string/replace % #":" ""))
    :validate [#(contains? #{:debug :info :warn :error} %) "Must be one of [:debug :info :warn :error]"]]
   ["-o" "--output-dir DIR" "Output directory for rendered plots"
    :parse-fn #(io/file %)
    :validate [#(.exists %) "Must exist on the filesystem"
               #(.isDirectory %) "Must be a directory"]]
   ["-d" "--input-dir SPECDIR" "Directory containing vega or vega-lite specs"
    :id :files
    :parse-fn #(io/file %)
    :validate [#(.exists %) "Must exist on the filesystem"
               #(.isDirectory %) "Must be a directory"
               #(seq (json-files %)) "Must contain at least one file ending in .json"]
    :assoc-fn (fn [m k v] (update m k concat (json-files v)))]
   ["-f" "--input-file SPEC" "File containing a vega or vega-lite spec"
    :id :files
    :parse-fn #(io/file %)
    :validate [#(.exists %) "Must exist on the filesystem"
               #(.isFile %) "Must be a file, not a directory"
               #(.endsWith (.getName %) ".json") "Must be a file ending with .json"]
    :assoc-fn (fn [m k v] (update m k conj v))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: planisphere [options] spec-type"
        "Spec Types:"
        " vega"
        " vega-lite"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (timbre/errorf "The following errors occurred while parsing your command:\n\n %s"
       (string/join \newline errors)))

(defn exit [status msg]
  (timbre/debugf "Exiting: %s \n %s" status msg)
  (System/exit status))

(defn path [file]
  (.getAbsolutePath file))

(defn parsed-files [files]
  (mapv (juxt #(.getName %) (comp parse-string slurp)) files))

(defn file->input [spec-type file]
  (zipmap [:type :id :spec]
          ((juxt
            (constantly spec-type)
            #(.getName %)
            (comp parse-string slurp)) file)))

(defn connection-failed-response [v e]
  (if e
    (-> (.getData e)
        :response
        precondition-failed?)
    false))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        {:keys [files output-dir help]} options
        spec-type (-> arguments first keyword)]
    (timbre/infof "Configuration options: %s" options)
    ;; Validate command line options
    (cond
      help (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (empty? files) (exit 1 (error-msg ["Missing input option. -h for help"]))
      (empty? arguments) (exit 1 (error-msg ["Missing spec type. -h for help"])))
    ;; Process files
    (planisphere/start-app (select-keys options [:port :host :headless? :log-level]))
    (doseq [file files
            :let [input (file->input spec-type file)]]
      ;; Retry send-spec a few times if our request
      ;; is sent out before things are finished booting up.
      (try+ (planisphere/validate-spec input)
            (catch Object _
              (timbre/error (:throwable &throw-context) "Spec validation error")
              (exit 1 nil)))
      (dh/with-retry {:retry-if connection-failed-response
                      :max-retries 10
                      :delay-ms 1000}
        (try+
         (let [{:keys [svg id] :as response} (planisphere/send-spec input)
               filename (format "%s/%s.jpg" (.getPath output-dir) (last id))]
           (timbre/infof "Saving: %s " filename)
           (planisphere/svg->jpeg filename svg))
         (catch Throwable t
           (get-thrown-object t)))))
    (exit 1 "Mission. COMPLETE!")))
