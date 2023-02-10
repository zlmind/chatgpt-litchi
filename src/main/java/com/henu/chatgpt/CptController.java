package com.henu.chatgpt;

import com.henu.chatgpt.client.ChatGptService;
import com.henu.chatgpt.dto.request.TextRequest;
import com.henu.chatgpt.dto.response.TextResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qinzheng
 */
@Controller
@RequestMapping("/chatGpt")
public class CptController {

    @Autowired
    public ChatGptService chatGptService;

    @GetMapping("/ask")
    @ResponseBody
    public String getInfo(@RequestParam("issue") String issue) {
        TextRequest textRequest = new TextRequest();
        textRequest.setPrompt(issue);
        String result;
        TextResponse textCompletion = chatGptService.createTextCompletion(textRequest);
        if (textCompletion.getChoices() != null && textCompletion.getChoices().get(0) != null) {
            result = textCompletion.getChoices().get(0).getText();
        } else {
            result = "OpenAI连接超时";
        }
        return result;
    }
}
