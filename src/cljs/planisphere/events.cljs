(ns planisphere.events
  (:require
   [planisphere.ws :as ws]
   [planisphere.utils :refer [chart->svg
                              parse-spec]]
   [re-frame.core :refer [reg-sub
                          reg-event-db
                          reg-event-fx
                          reg-fx
                          dispatch]]
   [day8.re-frame.async-flow-fx]))


(defn allocate-next-id
  "Returns the next todo id.
  Assumes todos are sorted.
  Returns one more than the current largest id."
  [plots]
  ((fnil inc 0) (first (:id (first plots)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Register Subscriptions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(reg-sub
 :plots
 (fn [db _]
   (:plots db)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Register Custom Event Handlers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(reg-fx
 :push
 (fn [value]
   (ws/send-message! value 8000)))

(reg-fx
 :vega
 (fn [{:keys [type id spec] :as m}]
   (parse-spec
    spec
    (fn [chart]
      (try
        (dispatch [:success-render (-> (assoc m :svg (chart->svg chart))
                                       (update :spec #(js/JSON.stringify % nil 2))
                                       (assoc :id [nil id]))])
        (catch js/Error error
          (.error js/console error)
          (dispatch [:fail-render [:planisphere/render-error
                                   (.-message error)]])))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Register Events
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(reg-event-db
 :initialize
 (fn
   [db _]
   (merge db {:plots (list)
              :render-flow-state {}})))

(defn spec-rendering-steps
  [{:keys [type id spec return] :as m}]
  {:id ::render-flow
   :db-path [:render-flow-state]
   :first-dispatch [:do-render m]
   :rules [{:when :seen? :events :success-render
            :dispatch [:push-results return]
            :halt? true}
           {:when :seen? :events :fail-render
            :halt? true}]})

;;:db (assoc (:db cofx) :spec spec)
(reg-event-fx
 :spec-received
 (fn [cofx [_ {:keys [type id spec] :as m}]]
   {:async-flow (spec-rendering-steps m)}))

(reg-event-fx
 :do-render
 (fn [_ [_ {:keys [type id spec] :as m}]]
   {:vega m}))

(reg-event-db
 :success-render
 (fn
   [db [_ value]]
   (let [id (allocate-next-id (:plots db))
         updated-value (assoc-in value [:id 0] id)]
     (update db :plots conj updated-value))))

(reg-event-fx
 :fail-render
 (fn [_ [_ value]]
   {:push value}))

(reg-event-fx
 :push-results
 (fn [cofx [_ value]]
   {:push [:planisphere/rendered (-> cofx :db :plots first (select-keys value))]}))
