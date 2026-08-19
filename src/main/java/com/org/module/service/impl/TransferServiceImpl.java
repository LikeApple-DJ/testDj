package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.entity.TransferRecord;
import com.org.module.mapper.TransferRecordMapper;
import com.org.module.service.TransferService;
import org.springframework.stereotype.Service;

@Service
public class TransferServiceImpl extends ServiceImpl<TransferRecordMapper, TransferRecord>
        implements TransferService {
}
