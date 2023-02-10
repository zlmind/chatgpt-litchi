package com.henu.chatgpt.client;

import com.henu.chatgpt.dto.request.ImageRequest;
import com.henu.chatgpt.dto.request.TextRequest;
import com.henu.chatgpt.dto.response.ImageResponse;
import com.henu.chatgpt.dto.response.TextResponse;

/**
 * @author qinzheng
 * @Description
 */
public interface ChatGptService {

    /**
     * text completion
     *
     * @param data
     * @return chatResponse
     */
    TextResponse createTextCompletion(TextRequest data);

    /**
     * image generation
     *
     * @param imageRequest
     * @return imageResponse
     */
    ImageResponse createImageGeneration(ImageRequest imageRequest);

}
