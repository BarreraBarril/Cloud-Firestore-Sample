package com.example.searchapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //DECLARACIÓN DE VARIABLES

    //Arreglo de objetos del tipo Person, para ser pasado posteriormente como parámetro al adaptador
    ArrayList<Person> personList = new ArrayList<>();

    //Objeto de tipo ListView que seráa utilizado para almacenar la información de las personas
    ListView myList;

    //Objeto de tipo PersonListAdapter (Definido en PersonListAdapter.java)
    PersonListAdapter adapter;

    //Objeto de tipo EditText que será utilizado para buscar las cadenas de carácteres ingresadas
    //por el usuario
    EditText textSearch;

    //Objeto de tipo FloatingActionButton que será utilizado para desplegar la actividad de adición de personas
    FloatingActionButton addButton;

    //Objeto de tipo FirebaseFirestore que será utilizado para representar la base de datos Firebase en la app
    FirebaseFirestore database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DEFINICIÓN DE VARIABLES

        //Asignación de la variable 'database' a la instancia de la base de datos firebase que será
        // utilizada por la aplicación.
        // La app se encuentra enlazada a la base de datos en Firebase mediante el archivo google-services.json,
        //ubicado en el directorio \app
        database = FirebaseFirestore.getInstance();

        //Buscar el widget listview  que será utilizado (Definido en activity_main.xml)
        myList = findViewById(R.id.listview);

        //Buscar el widget listview  que será utilizado (Definido en activity_main.xml)
        textSearch = findViewById(R.id.searchText);

        //Buscar el widget Floating Action Button  que será utilizado (Definido en activity_main.xml)
        addButton = findViewById(R.id.add_button);


        // Definición de acciones a realizar en caso de que cambie el texto del widget EditText
        //Para esto, se hace un llamado al método 'addTextChangedListener', propio de la clase EditText
        //Este método espera como parámetro un objeto del tipo TextWatcher, dentro del cual son definidos
        //métodos de "observación" a cambios en el EditTExt.
        //Es importante mencionar que pese a que los tres métodos requieren ser definidos, sólo se le asignará
        //acciones al método 'afterTextChanged', el cual determina las instrucciones que se requieren ejecutar
        //inmediatamente después de cada vez que el texto del EditText cambia.
        //Esto, debido a que es el único que requiere ser utiizado en el contexto de esta aplicación
        textSearch.addTextChangedListener(new TextWatcher() {

            //Definición del método afterTextChanged
            @Override
            public void afterTextChanged(Editable arg0) {
                //Se recibe el texto del EditText, convertido a string (por si existiesen caracteres
                // numéricos) y se se usa toLowerCase para no diferenciar entre mayúsculas y minúsculas
                String text = textSearch.getText().toString().toLowerCase(Locale.getDefault());
                //Se llama al método filter del adaptador, que tiene por objetivo filtrar la lista
                // recibiendo como parámetro el texto ingresado por el usuario
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
        });


        //Prevenir que el teclado se despliegue automaticamente al iniciar la activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        // Definición de acciones a realizar en caso de que el botón sea presionado.
        // Para esto, se hace un llamado al método 'setOnClickListener', propio de la clase FloatingActionButton
        // Este método espera como parámetro un objeto del tipo View.OnClickListener, dentro del cual son definidas
        // las acciones a ejecutar en caso de que el botón sea presionado
        addButton.setOnClickListener(new View.OnClickListener() {
            //LLamado al único método de la clase View.OnClickListener(), 'onClick', dentro del cual se explicitan
            // las instrucciones que se requieren ejecutar en caso de que el botón sea presionado
            @Override
            public void onClick(View view) {
                //Definición de un nuevo objeto de tipo Intent, el cual tiene como objetivo acceder a la actividad
                //AddPersonActivity
                Intent intent = new Intent(getApplicationContext(), AddPersonActivity.class);
                //Se inicia la actividad AddPersonActivity
                startActivity(intent);
            }
        });


    }

    //LLamado al método onResume propio de la clase AppCompatActivity, el cual es ejecutado cada vez que la actividad
    // se hace visible. Por lo tanto, también es llamado cuando se regresa a MainActivity desde AddPersonActivity
    @Override
    public void onResume(){
        super.onResume();
        Log.d("ON RESUMEEEEEEE", "onResume: ");
        //Se limpia el contenido anterior de la lista de personas (debido a que un nuevo elemento fue agregado)
        personList.clear();
        //Llamado a la funcíon 'getDocuments' (Definida más abajo)
        getDocuments();
    }


    //Función 'getDocuments() ', el cual obtiene datos desde la colección "persons" de la base de datos
    // Firebase, la cual almacena la información de la personas.
    //Lamamos "colección" a la lista completa de personas, y "documento" a cada persona individual.
    private void getDocuments() {
        //establecemos que queremos obtener los documentos de la la colección persons, ordenados de manera
        // descendente por su atributo "timestamp", el cual contiene la fecha y hora en el cual se almacenó dicho
        //documento. También limitamos la cantidad de elementos obtenidos a 50.
        database.collection("persons").orderBy("timestamp", Query.Direction.DESCENDING).limit(50)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //Método onComplete, que es ejecutado una vez que la operación de lectura de la base de datos
                    //ha sido completada. Dentro de éste método, podemos especificar que queremos hacer con los datos
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Por cada documento obtenido desde la base de datos, se crea un objeto de
                            //tipo persona, asignando a sus atributos los respectivos campos de la
                            //base de datos
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Cración de un nuevo objeto de tipo persona
                                Person person = new Person(
                                        document.getData().get("name").toString(),
                                        document.getData().get("lastName").toString(),
                                        document.getData().get("birthDate").toString(),
                                        document.getData().get("email").toString());
                                //Se añade el objeto creado a la lista de personas
                                personList.add(person);
                            }
                            //Inicialización del adaptador PersonListAdapter, haciendo uso de su constructor,
                            //el cual es definido en la clase PersonListAdapter.
                            //Este constructor recibe dos parámetros, un contexto (this) y una lista de objetos del tipo Person
                            adapter = new PersonListAdapter(getApplicationContext(), personList);
                            //Asignación del adaptador al ListView
                            myList.setAdapter(adapter);
                        } else {
                            //Imprimir un msj en consola en caso de error
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}