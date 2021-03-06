[#if hue_error = "true"]
     <div id="hue_api_error" class="aui-message error" style="display: block;">
                 <p class="title">
                     <span class="aui-icon icon-error hidden"></span>
                     <b>Error: HUE API not reachable</b>.<br/>Please check the configuration in the backend and your HUE Bridge.
                 </p>

     </div>
[#else]
    <div id="fieldArea_notification_hue_bulp" class="field-group required">
        <label for="notification_hue_bulp" id="fieldLabelArea_notification_hue_bulp">Bulb</label>
        <select id="hue_bulp" name="hue_bulp">
        [#list bulps as bulp]
            <option value="${bulp.id}"> ${bulp.name} </option>
        [/#list]
        </select>
    </div>

    <div id="fieldArea_notification_hue_state" class="field-group required">
        <label for="notification_hue_state" id="fieldLabelArea_notification_hue_state">Event Result</label>
        <select name="hue_state" size="1">
            <option [#if hue_state = "success"] selected[/#if]> success</option>
            <option [#if hue_state = "fail"] selected[/#if]> fail</option>
            <option [#if hue_state = "unknown"] selected[/#if]> unknown</option>
        </select>
    </div>

    <div id="fieldArea_notification_hue_color" class="field-group required">
        <label for="notification_hue_color" id="fieldLabelArea_notification_hue_color">Color</label>
        <select name="hue_color" size="1">
            <option [#if hue_color = "blue"] selected[/#if]> blue</option>
            <option [#if hue_color = "green"] selected[/#if]> green</option>
            <option [#if hue_color = "orange"] selected[/#if]> orange</option>
            <option [#if hue_color = "red"] selected[/#if]> red</option>
            <option [#if hue_color = "yellow"] selected[/#if]> yellow</option>

        </select>
    </div>

    <div id="fieldArea_notification_hue_alert" class="field-group required">
        <label for="notification_hue_alert" id="fieldLabelArea_notification_hue_alert">Alert</label>
        <select name="hue_alert" size="1">
            <option [#if hue_alert = "none"] selected[/#if]> none</option>
            <option [#if hue_alert = "select"] selected[/#if]> select</option>
            <option [#if hue_alert = "lselect"] selected[/#if]> lselect</option>
        </select>
    </div>

    <div id="fieldArea_notification_hue_reset" class="field-group required">
        <label for="notification_hue_reset" id="fieldLabelArea_notification_hue_reset">&nbsp;</label>
    [#if hue_reset?has_content]
        <input type="checkbox" value="true" name="hue_reset" [#if hue_reset = "true"] checked[/#if]  />  Reset Color to previous state after x ms
    [#else]
        <input type="checkbox" value="true" name="hue_reset" checked />  Reset Color to previous state after x ms
    [/#if]
    </div>


    [#if hue_reset_ms?has_content]
        [@ww.textfield labelKey="hue.reset_ms" name="hue_reset_ms" value='${hue_reset_ms}' required='true'/]
    [#else]
        [@ww.textfield labelKey="hue.reset_ms" name="hue_reset_ms" value="5000" required='true'/]
    [/#if]

 [/#if]


