package com.ambition.bi.manager;

import com.ambition.bi.common.ErrorCode;
import com.ambition.bi.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Ambition
 * @date 2023/11/16 13:43
 */
@Service
public class AiManager {


    @Resource
    private YuCongMingClient yuCongMingClient;

    public String doChat(long modelId,String content) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(content);
        BaseResponse<DevChatResponse> devChatResponseBaseResponse = yuCongMingClient.doChat(devChatRequest);
        System.out.println("devChatResponseBaseResponse = " + devChatResponseBaseResponse);
        if (devChatResponseBaseResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用AI接口失败");
        }
        return devChatResponseBaseResponse.getData().getContent();
    }


}
