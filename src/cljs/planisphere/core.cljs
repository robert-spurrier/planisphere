(ns ^:figwheel-always planisphere.core
  (:require
   [cljsjs.highlight]
   [cljsjs.highlight.langs.clojure]
   [cljs.pprint :refer [pprint]]
   [reagent.core :as reagent :refer [render]]
   [re-frame.core :refer [dispatch-sync
                          subscribe]]
   [planisphere.events :as events]
   [planisphere.ws :as ws]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; App
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn render-code [this]
  (->> this reagent/dom-node (.highlightBlock js/hljs)))

(defn code-view [o]
  (reagent/create-class
   {:reagent-render
    (fn [o]
      [:pre>code.clj o])
    :component-did-mount render-code
    :component-did-update render-code}))

(defn plot-item
  []
  (let [spec-showing? (reagent/atom false)]
    (fn [{:keys [id spec svg]}]
      [:div.pure-u-1
       [:div.pure-u-3-24]
       [:div.pure-u-18-24.segment.code
        [:div.pure-u-1.segment-main
         [code-view (with-out-str (pprint id))]]
        (if @spec-showing?
          [:div.pure-u-1.output
           {:on-click #(reset! spec-showing? false)}
           [code-view spec]]
          [:div.pure-u-1.output
           {:on-click #(reset! spec-showing? true)
            :dangerouslySetInnerHTML {:__html svg}}])]
       [:div.pure-u-3-24]])))

(defn plot-list
  []
  (let [plots (subscribe [:plots])]
    (fn []
      [:div.pure-u-1
       (for [entry @plots]
         (let [{:keys [id]} entry]
           ^{:key id} [plot-item entry]))])))

(defn app-view
  []
  [:div.pure-g
   [plot-list]])

(defn mount-components []
  (render [app-view] (.getElementById js/document "app")))

(defn init []
  (ws/start-router!)
  (dispatch-sync [:initialize]))

(defn ^:export run []
  (init)
  (mount-components))

(defn on-js-reload []
  (mount-components))
