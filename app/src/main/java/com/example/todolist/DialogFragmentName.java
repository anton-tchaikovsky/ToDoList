package com.example.todolist;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentName extends DialogFragment {
   private String descriptionName;
   private String title;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null){
            descriptionName = arguments.getString("DESCRIPTION_NAME");// текущее название или описание заметки
            title = arguments.getString("TITLE");//title для диалога
        }
        // надуваем view с полем для ввода названия или описания заметки
        View changeNameView = getLayoutInflater().inflate(R.layout.fragment_dialog_name,null);
        EditText editText = changeNameView.findViewById(R.id.new_name);
        editText.setText(descriptionName);

        return
        new AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setView(changeNameView)
                .setPositiveButton("Сохранить", (dialogInterface, i) -> {
                String newName = editText.getText().toString();
                    Bundle bundle = new Bundle();
                    // если назввние или описание заметки не введено или введены пробелы, то в заметке сохраняем ""
                    if (newName.equals(""))
                        bundle.putString("NEW_NAME", "");
                    else{
                        for (int j = 0; j<newName.length(); j++)
                            if (newName.charAt(j)!=' ')
                                bundle.putString("NEW_NAME", newName);
                            else if (j==newName.length()-1)
                                bundle.putString("NEW_NAME", "");
                    }
                    //записываем результат
                requireActivity().getSupportFragmentManager().setFragmentResult("KEY_NEW_NAME", bundle);
                dismiss();
                })
                .setNegativeButton("Отмена", null)
                .create();
    }

    public static DialogFragmentName newInstance (String descriptionName, String title) {
        Bundle args = new Bundle();
        args.putString("DESCRIPTION_NAME",descriptionName);
        args.putString("TITLE",title);
        DialogFragmentName fragment = new DialogFragmentName();
        fragment.setArguments(args);
        return fragment;
    }
}
