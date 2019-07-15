package com.example.searchapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

//Definición de la clase PersonListAdapter, el cual será utilizado para la crear la relación entre
//el listview y los elementos que se desean mostrar
public class PersonListAdapter extends BaseAdapter {

    //Declaración de variables context e inflater.
    //context se usa para establecer el contexto en que el layout será utilizado, e inflter
    //para instanciar dicho layout
    Context context;
    LayoutInflater inflater;

    //Lista completa de personas existentes
    private ArrayList<Person> fullPersonList;

    //Lista reducida de personas, pensada para cambiar en constantemente, en base a la búsqueda del
    //usuario
    private ArrayList<Person> filteredPersonList;


    //Constructor de la clase, el cual recibe como parámetros el contexto en que se utilizará
    //el adaptador, y una lista de personas para ser desplegada en el listview

    public PersonListAdapter(Context context, ArrayList<Person> personList) {
        this.context = context;
        this.filteredPersonList = personList;
        this.inflater = LayoutInflater.from(context);
        this.fullPersonList = new ArrayList<>();
        this.fullPersonList.addAll(personList);
    }

    //Clase que será utilizada para almacenar los TextView que serán incluidos en el listview
    public class ViewHolder {
        TextView nameText;
        TextView lastNameText;
        TextView birthDateText;
        TextView emailText;
    }

    //Métodos propios de la clase BaseAdapter, los cuales requieren ser definidos al heredar de esta clase,
    // sin embargo, ninguno de ellos es utilizado en el contexto de esta aplicación.
    @Override
    public int getCount() {
        return filteredPersonList.size();
    }
    @Override
    public Person getItem(int position) {
        return filteredPersonList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    //Método utilizado para obtener crear cada fila del listview
    @Override
    public View getView(final int position, View listViewItem, ViewGroup parent) {
        //Creación de instancia de la clase ViewHolder, la cual almacenar los TextView que
        // son incluidos en el listview
        final ViewHolder holder;

        //Si la fila es nula, se crea una nueva instancia de ViewHolder con cada uno de los textviews
        //que contiene
        if (listViewItem == null) {
            holder = new ViewHolder();

            //Se crea una fila de la lista, buscando el elemento listview_item.xml definido
            //en los layouts, el cual tiene por objetivo representar una fila la cual contiene
            //los cuatro TextView correspondientes
            listViewItem = inflater.inflate(R.layout.listview_item, null);

            // Se ubica cada TextView en listview_item.xml
            holder.nameText = listViewItem.findViewById(R.id.name_text);
            holder.lastNameText =  listViewItem.findViewById(R.id.last_name_text);
            holder.birthDateText = listViewItem.findViewById(R.id.birth_date_text);
            holder.emailText = listViewItem.findViewById(R.id.email_text);

            //Se usa este método (SetTag) para diferenciar entre cada fila de la lista
            // (Es necesario)
            listViewItem.setTag(holder);
        } else {
            //En caso de que la fila no sea nula, simplemente se usa getTag para inicializar
            //la fila
            holder = (ViewHolder) listViewItem.getTag();
        }
        // Se asignan los elementos para cada fila a partir de la lista filtrada de personas
        holder.nameText.setText(filteredPersonList.get(position).getName());
        holder.lastNameText.setText(filteredPersonList.get(position).getLastName());
        holder.birthDateText.setText(filteredPersonList.get(position).getBirthDate());
        holder.emailText.setText(filteredPersonList.get(position).getEmail());
        return listViewItem;
    }

    // Método que será utilizado para filtrar la lista de personas, según el texto ingresado
    // por el usuario.
    // Este método recibe un String llamado inputText, el cual representa el texto
    // presente en el EditText.
    public void filter(String inputText) {

        // se usa toLowerCase para no diferenciar entre mayúsculas y minúsculas
        inputText = inputText.toLowerCase(Locale.getDefault());

        //Se vacía la lista anterior, para ingresar a la lista elementos nuevos
        filteredPersonList.clear();
        //Condicional if que establece que: en el caso de que el largo del inputText sea igual a 0
        // (lo que significa que el EditText se encuentra vacío, y el usuario no está buscando nada
        // actualmente), la lista filtrada de personas es igual a la lista completa

        if (inputText.length() == 0) {
            filteredPersonList.addAll(fullPersonList);
        }
        //En caso de que el inputText no tenga longitud 0, se procede a filtrar la lista, haciendo uso
        //de un ciclo for que recorre todos los elementos de la lista, y aplicando un condiciconal if
        // que revisa si es que alguno de los atributos de la persona contiene la cadena de caracteres
        //ingresada por el usuario.
        else {
            for (Person person : fullPersonList) {
                if (person.getName().toLowerCase(Locale.getDefault()).contains(inputText)
                || person.getLastName().toLowerCase(Locale.getDefault()).contains(inputText)
                || person.getBirthDate().toLowerCase(Locale.getDefault()).contains(inputText)
                || person.getEmail().toLowerCase(Locale.getDefault()).contains(inputText)) {
                    //En caso de contener la cadena, se agregan dichas persona a la lista
                    filteredPersonList.add(person);
                }
            }
        }
        //Es necesario llamar a notifyDataSetChanged, cada vez que es modificado el contenido de la
        // lista, con el fin de avisar al adaptador y realizar la actualización visual
        notifyDataSetChanged();
    }
}