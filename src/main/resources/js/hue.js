
AJS.$(function(){
    AJS.$("#content-2").hide();
    AJS.$("#general-nav-item").click(function(e){
        navigateTo(e.target, "content-1");
    });
    AJS.$("#advanced-nav-item").click(function(e){
        navigateTo(e.target, "content-2");
    });
    function navigateTo(trigger, contentId){
        AJS.$("#main-nav li").removeClass("aui-nav-selected");
        AJS.$(trigger).parent().addClass("aui-nav-selected");
        AJS.$(".nav-content").hide();
        AJS.$("#" + contentId).show();
    }
})