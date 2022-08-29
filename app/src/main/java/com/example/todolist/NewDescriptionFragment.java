package com.example.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;


public class NewDescriptionFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //создаем view с полями для ввода названия и описания заметки и с календарем для выбора даты заметки
        View view = getLayoutInflater().inflate(R.layout.fragment_new_description,null);
        EditText newName = view.findViewById(R.id.new_name);
        EditText newDetails = view.findViewById(R.id.new_description);
        DatePicker calendar = view.findViewById(R.id.new_calendar);
        Calendar date = Calendar.getInstance();
        calendar.init(date.get(Calendar.YEAR), date.get (Calendar.MONTH), date.get (Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> date.set(i, i1, i2));

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Внесите данные заметки")
                .setView(view)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Создать", (dialogInterface, i) -> {
                    // в bundle записываем название и описание заметки, дату заметки
                    // если название или описание не введены или введены пробелы, то в bundle передаем ""
                    Bundle bundle = new Bundle();
                  if (newName.getText().toString().equals(""))
                      bundle.putString("NEW_NAME", "");
                  else{
                      for (int j = 0; j<newName.getText().toString().length(); j++)
                          if (newName.getText().toString().charAt(j)!=' ')
                              bundle.putString("NEW_NAME", newName.getText().toString());
                          else if (j==newName.getText().toString().length()-1)
                              bundle.putString("NEW_NAME", "");
                  }
                    if (newDetails.getText().toString().equals(""))
                        bundle.putString("NEW_DETAILS", "");
                    else{
                        for (int j = 0; j<newDetails.getText().toString().length(); j++)
                            if (newDetails.getText().toString().charAt(j)!=' ')
                                bundle.putString("NEW_DETAILS", newDetails.getText().toString());
                            else if (j==newDetails.getText().toString().length()-1)
                                bundle.putString("NEW_DETAILS", "");
                    }
                  bundle.putSerializable("NEW_DATE", date);
                  requireActivity().getSupportFragmentManager().setFragmentResult("NEW_DESCRIPTION", bundle);
                  dismiss();
                })
                .create();

    }

    public static NewDescriptionFragment newInstance() {
        return new NewDescriptionFragment();
    }
}