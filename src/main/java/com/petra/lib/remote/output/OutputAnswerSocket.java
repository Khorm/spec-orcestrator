package com.petra.lib.remote.output;

import com.petra.lib.remote.signal.AnswerDto;
import com.petra.lib.remote.signal.Signal;

/**
 * �����, ��������� �� ��������� ������������� ���������
 */
public interface OutputAnswerSocket {
    AnswerDto answer(Signal signal);
}
