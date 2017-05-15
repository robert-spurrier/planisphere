(ns planisphere.api.validation
  (:require
   [clojure.java.io :refer [resource]]
   [mount.core :as mount :refer [defstate]]
   [cprop.core :refer [load-config]]
   [cheshire.core :refer [parse-string]]
   [scjsv.core :as v]))

(defn- validate-fn [validators]
  (fn [{:keys [type spec]}]
    (let [parsed-spec (cond
                        (map? spec) spec
                        (string? spec) (parse-string spec keyword))]
      ((type validators) parsed-spec))))

(defn- spec-validator [fname]
  (-> fname resource slurp v/validator))

(defstate ^{:on-reload :noop}
  valid?
  :start (let [{:keys [vega-schema vega-lite-schema]} (mount/args)]
           (validate-fn {:vega (spec-validator vega-schema)
                         :vega-lite (spec-validator vega-lite-schema)})))
