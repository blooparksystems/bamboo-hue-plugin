<html>
<head>
    <title>[@ww.text name='Bloopark Hue Plugin Configuration' /]</title>
    <meta name="decorator" content="adminpage">
</head>
<body>
<h1>[@ww.text name='Bloopark Hue Plugin Configuration' /]</h1>
<div class="paddedClearer" ></div>
[@ww.form action="configureHue"
namespace="/admin"
id="blooparkHueConfigurationForm"
submitLabelKey='global.buttons.update'
cancelUri='/admin/administer.action'
titleKey='Configure Bloopark Hue Plugin'
]

    [@ww.textfield
    id='blooparkHueHost'
    name='blooparkHueHost'
    label='Hue URL'
    required='true'
    description='The URL to the hue box'
    /]

    [@ww.textfield
    id='blooparkHuePort'
    name='blooparkHuePort'
    label='Hue API Port'
    required='true'
    description='Port, default is 80'
    /]

    [@ww.textfield
    id='blooparkHueUser'
    name='blooparkHueUser'
    label='Hue API User'
    required='true'
    description='The API user'
    /]
[/@ww.form]
</body>
</html>
