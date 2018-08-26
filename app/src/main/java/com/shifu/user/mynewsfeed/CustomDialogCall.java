package com.shifu.user.mynewsfeed;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class CustomDialogCall {

    static Observable<Integer> showRadioButtonDialog(Context context) {

        PublishSubject<Integer> publishSubject = PublishSubject.create();

        final Dialog dialog = new Dialog(context, R.style.Theme_NNN_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories);
        dialog.setCancelable(true);

        String[] buttonsText = context.getResources().getStringArray(R.array.categories);

        RadioGroup rg = dialog.findViewById(R.id.radio_group);
        for(int i=0; i< buttonsText.length; i++){
            RadioButton rb = new RadioButton(context);
            rb.setText(buttonsText[i].substring(buttonsText[i].indexOf('|')+1));
            rb.setTextColor(context.getResources().getColor(R.color.textColorPrimary));
            rb.setId(i);
            rg.addView(rb);
        }

        Button choose = dialog.findViewById(R.id.button_choose);
        choose.setOnClickListener(view -> {
            if (rg.getCheckedRadioButtonId() != -1) {

                publishSubject.onNext(rg.getCheckedRadioButtonId());
                dialog.cancel();
            }
        });

        Button cansel = dialog.findViewById(R.id.button_cansel);
        cansel.setOnClickListener(view -> {
            publishSubject.onNext(-1);
            dialog.cancel();
        });

        return publishSubject
                .hide()
                .doOnSubscribe(display -> dialog.show());
    }
}
