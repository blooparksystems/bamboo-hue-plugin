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