$(document).ready(function () {
    $(".run").bind("click", function () {
        $(".lexical").val("");
        var code = editor.getValue();
        if (!isNull(code)) {
            $.ajax({
                url: "/run",
                type: "POST",
                sync: true,
                data: {code: code},
                success: function (resultData) {
                    if (resultData.status == 1) {
                        var tokens = resultData.lexical;
                        if (tokens != null) {
                            var result = "";
                            for (var i = 0; i < tokens.length; i++) {
                                result += tokens[i].value;
                                result += "\n";
                            }
                            $(".item-lexical").css("border-color", "rgb(0, 255, 0)");
                            $(".item-content-lexical").css("border-top-color", "rgb(0, 255, 0)");
                            $("#item-status-lexical").attr("src", "/images/success.png");
                            $(".lexical").val(result);
                        }
                    } else if (resultData.status == -1) {
                        $(".item-lexical").css("border-color", "rgb(255, 0, 0)");
                        $(".item-content-lexical").css("border-top-color", "rgb(255, 0, 0)");
                        $("#item-status-lexical").attr("src", "/images/fail.png");
                        $(".lexical").val(resultData.msg);
                    }
                }
            });
        }
    });
});

function isNull(param) {
    if (param != null) {
        param = param.replace(/^\s*|\s*$/g, "");
        if (param.length > 0) {
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}