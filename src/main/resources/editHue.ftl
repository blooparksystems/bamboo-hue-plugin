[#if hue_host?has_content]
    [@ww.textfield labelKey="hue.host" name="hue_host" value='${hue_host}' required='true'/]
[#else]
    [@ww.textfield labelKey="hue.host" name="hue_host" value="" required='true'/]
[/#if]

[#if hue_port?has_content]
    [@ww.textfield labelKey="hue.port" name="hue_port" value='${hue_port}' required='true'/]
[#else]
    [@ww.textfield labelKey="hue.port" name="hue_port" value="80" required='true'/]
[/#if]

[#if hue_username?has_content]
    [@ww.textfield labelKey="hue.username" name="hue_username" value='${hue_username}' required='true'/]
[#else]
    [@ww.textfield labelKey="hue.username" name="hue_username" value="" required='true'/]
[/#if]

[#if hue_ids?has_content]
    [@ww.textfield labelKey="hue.ids" name="hue_ids" value='${hue_ids}' required='true'/]
[#else]
    [@ww.textfield labelKey="hue.ids" name="hue_ids" value="1,2,3,4" required='true'/]
[/#if]


<div id="fieldArea_notification_hue_reset" class="field-group required">
    <label for="notification_hue_reset" id="fieldLabelArea_notification_hue_reset">&nbsp;</label>
    <input type="checkbox" value="true" name="hue_reset" [#if hue_reset = "true"] checked[/#if] />  Reset Color to previous state after x ms
</div>


[#if hue_reset_ms?has_content]
    [@ww.textfield labelKey="hue.reset_ms" name="hue_reset_ms" value='${hue_reset_ms}' required='true'/]
[#else]
    [@ww.textfield labelKey="hue.reset_ms" name="hue_reset_ms" value="5000" required='true'/]
[/#if]



<div id="fieldArea_notification_hue_color_success" class="field-group required">
    <label for="notification_hue_color_success" id="fieldLabelArea_notification_hue_color_success">Color on success</label>
    <select name="hue_color_success" size="1">
        <option [#if hue_color_success = "blue"] selected[/#if]> blue</option>
        <option [#if hue_color_success = "green"] selected[/#if]> green</option>
        <option [#if hue_color_success = "orange"] selected[/#if]> orange</option>
        <option [#if hue_color_success = "red"] selected[/#if]> red</option>
        <option [#if hue_color_success = "yellow"] selected[/#if]> yellow</option>

    </select>
</div>

<div id="fieldArea_notification_hue_color_fail" class="field-group required">
    <label for="notification_hue_color_fail" id="fieldLabelArea_notification_hue_color_fail">Color on failure</label>
    <select name="hue_color_failure" size="1">
        <option [#if hue_color_failure = "blue"] selected[/#if]> blue</option>
        <option [#if hue_color_failure = "green"] selected[/#if]> green</option>
        <option [#if hue_color_failure = "orange"] selected[/#if]> orange</option>
        <option [#if hue_color_failure = "red"] selected[/#if]> red</option>
        <option [#if hue_color_failure = "yellow"] selected[/#if]> yellow</option>

    </select>
</div>


