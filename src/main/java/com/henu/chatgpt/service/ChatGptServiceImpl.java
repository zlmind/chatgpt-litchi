package com.henu.chatgpt.service;

import com.alibaba.fastjson.JSONObject;
import com.henu.chatgpt.client.ChatGptService;
import com.henu.chatgpt.dto.request.ImageRequest;
import com.henu.chatgpt.dto.request.TextRequest;
import com.henu.chatgpt.dto.response.ImageResponse;
import com.henu.chatgpt.dto.response.TextResponse;
import com.henu.chatgpt.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qz
 */
@Slf4j
@Service
public class ChatGptServiceImpl implements ChatGptService {

    @Value("${openai.key}")
    private String apiKey;

    private final Map<String, String> header = new HashMap<>();

    private static final String TEXT_COMPLETION_URL = "https://api.openai.com/v1/completions";

    private static final String IMAGE_GENERATE_URL = "https://api.openai.com/v1/images/generations";

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(apiKey)) {
            apiKey = System.getenv("openai.apikey");
        }
        Assert.isTrue(!StringUtils.isEmpty(apiKey), "openai apiKey not exist");
        header.put("Authorization", "Bearer " + apiKey);
    }

    @Override
    public TextResponse createTextCompletion(TextRequest textRequest) {
        TextResponse response = new TextResponse();
        try {
            JSONObject jsonObject = HttpClientUtil.sendHttp(TEXT_COMPLETION_URL, JSONObject.toJSONString(textRequest), header);
            response = JSONObject.parseObject(jsonObject.toString(), TextResponse.class);
        } catch (Throwable t) {
            log.error("createCompletion failed, data: {}, t: ",
                    JSONObject.toJSONString(textRequest), t);
        }
        return response;
    }

    @Override
    public ImageResponse createImageGeneration(ImageRequest imageRequest) {
        ImageResponse response = new ImageResponse();
        try {
            JSONObject jsonObject = HttpClientUtil.sendHttp(IMAGE_GENERATE_URL, JSONObject.toJSONString(imageRequest), header);
            response = JSONObject.parseObject(jsonObject.toString(), ImageResponse.class);
        } catch (Throwable t) {
            log.error("createCompletion failed, data: {}, t: ",
                    JSONObject.toJSONString(imageRequest), t);
        }
        return response;
    }
}
