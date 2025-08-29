package com.petra.lib.block;

import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.RemoteProducer;

@Deprecated
public interface BlockInfo {
    LocalConsumer getConsumer();
    RemoteProducer getProducer();
}
