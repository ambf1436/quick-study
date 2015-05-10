package com.gcw_rome_2014.quickstudy.model.events;

import com.gcw_rome_2014.quickstudy.R;
import com.gcw_rome_2014.quickstudy.model.Exam;
import com.gcw_rome_2014.quickstudy.model.QuickStudy;

/**
 * Created by Luigi on 09/03/2015.
 * Class that represent an Exam event on calendar.
 */
public class ExamEvent extends Event {

    public ExamEvent(Exam exam) {
        super(exam,
                getContext().getResources().getString(R.string.exam_in) + " " + exam.getName(),
                QuickStudy.getAppName() + " Automatic Planner",
                0);
    }

}