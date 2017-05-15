(ns planisphere.utils
  (:require
   [cljsjs.vega]
   [cljsjs.vega-lite]))

(defmulti spec-handler
  (fn [{:keys [type id spec] :as m}] type))

(defmethod spec-handler :vega
  [{:keys [type id spec] :as m}]
  (update m :spec clj->js))

(defmethod spec-handler :vega-lite
  [{:keys [type id spec] :as m}]
  (try
    (update m :spec #(.-spec (js/vl.compile (clj->js %))))
    (catch js/Error e
      (.log js/console e))))

(defn chart->svg
  [chart]
  (let [c (.update (chart #js {:renderer "svg"}))
        svg (.svg c)]
    svg))

(defn parse-spec
  [spec chart-fn]
  (js/vg.parse.spec spec chart-fn))
