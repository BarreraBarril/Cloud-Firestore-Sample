package com.example.searchapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPersonActivity extends AppCompatActivity  {

    //Objeto de tipo EditText que será utilizado para ingresar el nombre de a persona
    EditText inputName;

    //Objeto de tipo EditText que será utilizado para ingresar el email de a persona

    EditText inputLastName;

    //Objeto de tipo EditText que será utilizado para ingresar la fecha de nacimiento de la persona

    EditText inputBirthDate;

    //Objeto de tipo EditText que será utilizado para ingresar el apellido de a persona

    EditText inputEmail;

    //Objeto de tipo Button que será utilizado para concretar el almacenamiento de la persona

    Button addPersonButton;
    //Objeto de tipo ImageButton que será utilizado para desplegar la selección de fecha

    ImageButton calendarButton;

    //Objeto de tipo FirebaseFirestore que será utilizado para representar la base de datos Firebase en la app
    FirebaseFirestore database;

    //objeto de tipo calendar, usado para almacenar una fecha
    public final Calendar c = Calendar.getInstance();

    //Obtención de mes actual
    final int actualMonth = c.get(Calendar.MONTH);
    //Obtención de día actual
    final int actualDay = c.get(Calendar.DAY_OF_MONTH);
    //Obtención de año actual
    final int actualYear = c.get(Calendar.YEAR);

    //Formato que se utilizará para validar el email para el email
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        //Buscar los widgets EditText que serán utilizados (Definidos en acitivity_add_person.xml)
        inputName = findViewById(R.id.input_name);
        inputLastName = findViewById(R.id.input_last_name);
        inputBirthDate = findViewById(R.id.input_birth_date);
        inputEmail = findViewById(R.id.input_email);

        //Buscar el widget ImageButton que será utilizado (Definido en acitivity_add_person.xml)
        calendarButton = findViewById(R.id.imageButton1);

        //Buscar el widget Button que será utilizado (Definido en acitivity_add_person.xml)
        addPersonButton = findViewById(R.id.button_add_person);

        //Asignación de la variable 'database' a la instancia de la base de datos firebase que será
        // utilizada por la aplicación.
        // La app se encuentra enlazada a la base de datos en Firebase mediante el archivo google-services.json,
        //ubicado en el directorio \app
        database = FirebaseFirestore.getInstance();


        // Definición de acciones a realizar en caso de que el botón sea presionado.
        // Para esto, se hace un llamado al método 'setOnClickListener', propio de la clase ImageButton
        // Este método espera como parámetro un objeto del tipo View.OnClickListener, dentro del cual son definidas
        // las acciones a ejecutar en caso de que el botón sea presionado
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LLamado a la función getDate, definida más abajo
                getDate();
            }
        });

        // Definición de acciones a realizar en caso de que el botón sea presionado.
        // Para esto, se hace un llamado al método 'setOnClickListener', propio de la clase Button
        // Este método espera como parámetro un objeto del tipo View.OnClickListener, dentro del cual son definidas
        // las acciones a ejecutar en caso de que el botón sea presionado
        addPersonButton.setOnClickListener(new View.OnClickListener() {

            //LLamado al único método de la clase View.OnClickListener(), 'onClick', dentro del cual se explicitan
            // las instrucciones que se requieren ejecutar en caso de que el botón sea presionado
            @Override
            public void onClick(View view) {

                //Verificar que ningún campo este vacío
                if (inputName.getText().toString().isEmpty() ||
                        inputLastName.getText().toString().isEmpty() ||
                        inputBirthDate.getText().toString().isEmpty() ||
                        inputEmail.getText().toString().isEmpty()
                )  {
                    //En caso de que no se existan campos vacíos
                    Toast.makeText(getApplicationContext(), "Ingrese todos los datos solicitados", Toast.LENGTH_SHORT).show();
                } else {
                    //Conversión de los textos de los campos de de nombre y apellido, al formato
                    // primera letra mayúscula, resto minúsculas
                    String capName = inputName.getText().toString().substring(0, 1).toUpperCase() + inputName.getText().toString().substring(1).toLowerCase();
                    String capLastName = inputLastName.getText().toString().substring(0, 1).toUpperCase() + inputLastName.getText().toString().substring(1).toLowerCase();
                    if (inputName.length() >= 3 && inputLastName.length() >= 3){
                        //Revisar si es que el email ingresado se adapta el formato esperado
                        if (inputEmail.getText().toString().trim().matches(emailPattern)) {
                            //Creción de un nuevo objeto de tipo MAP <String, Object>, cuyos elementos consisten en un par
                            //de 1) el nombre del atributo en la base de datos y 2) el valor que se quiere almacenar en dicho
                            //atributo. Para eso, se asignan como valores los textos que se encuentran en los EditText
                            Map<String, Object> person = new HashMap<>();
                            person.put("name", capName);
                            person.put("lastName", capLastName);
                            person.put("birthDate", inputBirthDate.getText().toString());
                            person.put("email", inputEmail.getText().toString());
                            //atributo timestamp que almacena el momento en que el objeto fue creado
                            person.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                            //se agrega el objeto a la colección persons
                            database.collection("persons").add(person);
                            //llamado a la función realTimeUpdate, definida más abajo
                            realTimeUpdate();
                            //se termina esta actividad y se regresa a la anterior. El método finish es propio de la
                            //clase activity
                            finish();

                            //En caso de que no se ingrese un email con el formato correcto
                        } else {
                            Toast.makeText(getApplicationContext(), "Ingrese un email correcto", Toast.LENGTH_SHORT).show();
                        }
                }
                    else {
                        Toast.makeText(getApplicationContext(), "Ingrese al menos tres caracteres en nombre y apellido", Toast.LENGTH_SHORT).show();
                    }
                }

            }


        });
    }

    //Función 'getDate', la cual será utilizada para desplegar el cuadro de díalogo de selección de fecha
    //de nacimiento
    private void getDate() {
        //se crea un nuevo objeto DatePickerDialog, el cual será utilizado para seleccionar la fecha de nacimiento
        //de la persona
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Muestra la fecha con el formato deseado
                month = month + 1;
                inputBirthDate.setText(dayOfMonth + "/" + month + "/" + year);
            }
            //valores por defecto que serán mostrados en el cuadro de díalogo (fecha actual)
            }, actualYear, actualMonth, actualDay);

        //Creación de objetos de tipo Calendar para referir a las fechas de nacimiento límites que se permitirán. Éstas serán,
        // de 5 a 100 años de edad
        Calendar minCal = Calendar.getInstance();
        minCal.set(Calendar.YEAR, minCal.get(Calendar.YEAR) - 100);
        Calendar maxCal = Calendar.getInstance();
        maxCal.set(Calendar.YEAR, maxCal.get(Calendar.YEAR) - 5);
        datePickerDialog.getDatePicker().setMinDate(minCal.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCal.getTimeInMillis());

        //Se mustra el cuadro de diálogo
        datePickerDialog.show();
    }

    //función realTimeUpdate, usado para avisar a la aplicación que el elemento recién creado ha sido
    //guardado en Firebase. Esto, para volver oporunamente a la actividad anterior y ver el elemento
    //recién creado en la lista (En caso contrario, se volvería a la lista de personas sin asegurar
    // que el nuevo elemento ya se encuentra almacenado y podría no aparecer)
    private void realTimeUpdate() {
        CollectionReference contactListener = database.collection("persons");
        //Espera al evento de almacenamiento del elemento creado
        contactListener.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshot, FirebaseFirestoreException e) {
                //Si es que no existen excepciones, retornar
                if (e != null) {
                    return;
                }
            }
        });
    }

}
