(ns status-im.ui.screens.offline-messaging-settings.edit-mailserver.views
  (:require-macros [status-im.utils.views :as views])
  (:require
   [re-frame.core :as re-frame]
   [status-im.ui.components.react :as react]
   [status-im.i18n :as i18n]
   [status-im.ui.components.colors :as colors]
   [status-im.ui.components.icons.vector-icons :as vector-icons]
   [status-im.ui.components.styles :as components.styles]
   [status-im.ui.components.common.common :as components.common]
   [status-im.ui.components.status-bar.view :as status-bar]
   [status-im.ui.components.toolbar.view :as toolbar]
   [status-im.ui.components.list.views :as list]
   [status-im.ui.components.text-input.view :as text-input]
   [status-im.ui.screens.offline-messaging-settings.edit-mailserver.styles :as styles]))

(defn connect-button [id]
  [react/touchable-highlight {:on-press #(re-frame/dispatch [:connect-wnode id])}
   [react/view styles/connect-button-container
    [react/view {:style               styles/connect-button
                 :accessibility-label :mailserver-connect-button}
     [react/text {:style      styles/connect-button-label
                  :uppercase? true}
      (i18n/label :t/connect)]]]])

(def qr-code
  [react/touchable-highlight {:on-press #(re-frame/dispatch [:scan-qr-code
                                                             {:toolbar-title (i18n/label :t/add-mailserver)}
                                                             :set-mailserver-from-qr])
                              :style    styles/qr-code}
   [react/view
    [vector-icons/icon :icons/qr {:color colors/blue}]]])

(views/defview edit-mailserver []
  (views/letsubs [manage-mailserver [:get-manage-mailserver]
                  is-valid?         [:manage-mailserver-valid?]]
    (let [url  (get-in manage-mailserver [:url :value])
          id   (get-in manage-mailserver [:id :value])
          name (get-in manage-mailserver [:name :value])]
      [react/view components.styles/flex
       [status-bar/status-bar]
       [react/keyboard-avoiding-view components.styles/flex
        [toolbar/simple-toolbar (i18n/label :t/add-mailserver)]
        [react/scroll-view
         [react/view styles/edit-mailserver-view
          [text-input/text-input-with-label
           {:label           (i18n/label :t/name)
            :placeholder     (i18n/label :t/specify-name)
            :style           styles/input
            :container       styles/input-container
            :default-value   name
            :on-change-text  #(re-frame/dispatch [:mailserver-set-input :name %])
            :auto-focus      true}]
          [text-input/text-input-with-label
           {:label           (i18n/label :t/mailserver-address)
            :placeholder     (i18n/label :t/specify-mailserver-address)
            :content         qr-code
            :style           styles/input
            :container       styles/input-container
            :default-value   url
            :on-change-text  #(re-frame/dispatch [:mailserver-set-input :url %])}]
          (when id [connect-button id])]]
        [react/view styles/bottom-container
         [react/view components.styles/flex]
         [components.common/bottom-button
          {:forward?  true
           :label     (i18n/label :t/save)
           :disabled? (not is-valid?)
           :on-press  #(re-frame/dispatch [:upsert-mailserver])}]]]])))
