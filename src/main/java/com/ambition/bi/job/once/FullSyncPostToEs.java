package com.ambition.bi.job.once;

import com.ambition.bi.model.entity.Post;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;

/**
 * 全量同步帖子到 es
 *
 * @author ambition
 */
// TODO 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {

    }


}
