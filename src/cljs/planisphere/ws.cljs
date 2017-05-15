(ns planisphere.ws
  (:require
   [planisphere.utils :refer [spec-handler]]
   [re-frame.core :refer [dispatch dispatch-sync]]
   [taoensso.encore :as encore :refer-macros (have have?)]
   [taoensso.timbre :as timbre :refer-macros [tracef debugf debug infof warnf errorf]]
   [taoensso.sente :as sente :refer [cb-success?]]
   [taoensso.sente.packers.transit :as sente-transit]))

(timbre/set-level! :info)

(let [packer (sente-transit/get-transit-packer)
      connection (sente/make-channel-socket! "/ws" {:type :auto
                                                    :packer packer})]
  (def ch-chsk (:ch-recv connection))
  (def send-message! (:send-fn connection)))

(defmulti -event-msg-handler :id)

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (debugf "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (if (:first-open? new-state-map)
      (debugf "Channel socket successfully established!: %s" ?data)
      (debugf "Channel socket state change: %s" ?data))))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (let [[id value] ?data]
    (case id
      :planisphere/spec (let [render-msg (spec-handler value)]
                          (dispatch [:spec-received render-msg]))
      :planisphere/log-level (timbre/set-level! value)
      :default (debugf "Push event from server: %s" ?data))))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (debugf "Handshake: %s" ?data)))

(defn event-msg-handler [{:as ev-msg :keys [id ?data event]}]
  (do (debugf "Event: %s" event)
      (-event-msg-handler ev-msg)))

(def router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router] (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router (sente/start-chsk-router!
                  ch-chsk
                  event-msg-handler)))
