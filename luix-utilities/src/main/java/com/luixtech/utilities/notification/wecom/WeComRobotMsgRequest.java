package com.luixtech.utilities.notification.wecom;

import lombok.Data;

@Data
public class WeComRobotMsgRequest {
    public static final String MSG_TYPE_TEXT     = "text";
    public static final String MSG_TYPE_MARKDOWN = "markdown";
    private             String msgtype;
    private             Text   text;
    private             Markdown   markdown;

    @Data
    public static class Text {
        private String   content;
        private String[] mentioned_list;
        private String[] mentioned_mobile_list;
    }

    @Data
    public static class Markdown {
        private String   content;
        private String[] mentioned_list;
        private String[] mentioned_mobile_list;
    }
}
