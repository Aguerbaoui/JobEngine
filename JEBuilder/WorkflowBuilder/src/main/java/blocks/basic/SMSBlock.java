package blocks.basic;

import blocks.WorkflowBlock;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SMSBlock extends WorkflowBlock {

        //Constants
        private String serverType;
        private List<String> receiverPhoneNumbers;
        private String accountSID;
        private String accountToken;
        private String senderPhoneNumber;
        private String message;
        private String inputType;
        private String URI;
        private String validity;
        private String smsType;
        private String modem;
        private boolean priority;
        private boolean sendAsUnicode;
        private boolean twilioServer;

;
        /*public String formatMessage() {
            String msg = getMessage();
            Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");

            Matcher matcher = pattern.matcher(msg);
            ArrayList<String> wordsToBeReplaced = new ArrayList<String>();
            while (matcher.find()) {
                wordsToBeReplaced.add(matcher.group());
            }
            for (String word : wordsToBeReplaced) {
                String tword = word.replace("${", "");
                String tword2 = tword.replace("}", "");
                msg = msg.replace(word, "\" + " + tword2 + " + \"");
            }
            return msg;
        }*/
    }
