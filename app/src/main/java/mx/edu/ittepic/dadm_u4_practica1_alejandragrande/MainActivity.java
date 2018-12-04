package mx.edu.ittepic.dadm_u4_practica1_alejandragrande;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText identificador, nombre, ingredientes, preparacion, observaciones;
    Button agregar, eliminar,actualizar,  consultar;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identificador=findViewById(R.id.identificarcampo);
        nombre=findViewById(R.id.nombrecampo);
        ingredientes=findViewById(R.id.ingredientecampo);
        preparacion=findViewById(R.id.preparacioncampo);
        observaciones=findViewById(R.id.observacionescampo);

        agregar=findViewById(R.id.agregar);
        consultar=findViewById(R.id.consultar);
        actualizar=findViewById(R.id.actualizar);
        eliminar=findViewById(R.id.eliminar);


        base=new BaseDatos(this, "primera", null, 1);//primera es el nombre de la base de datos
       agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoAgregar();
            }
        });
       consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });
       actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actualizar.getText().toString()=="Confirmar Actualización")
                {
                    invocarConfirmacionActualizar();
                }else {
                    pedirID(2);
                }

            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pedirID(3);

            }
        });
    }
    private void invocarConfirmacionActualizar() {
        AlertDialog.Builder confirmar=new AlertDialog.Builder(this);
        confirmar.setTitle("IMPORTANTE!!")
                .setMessage("¿Estas seguro que deseas actualizar?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizarDatos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesyLimpiarCampos();
                dialog.cancel();
            }
        }).show();

    }
    private void buscarDato(String idABuscar, int origen)
    {
        try
        {
            SQLiteDatabase tabla=base.getReadableDatabase();
            String SQL ="SELECT * FROM RECETA  WHERE ID="+idABuscar;

            Cursor resultado=tabla.rawQuery(SQL, null);



            if (resultado.moveToFirst())
            {
                //si hay

                if(origen==3)
                {
                    String datos=resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4);
                    invocarConfirmacionEliminar(datos, idABuscar);
                    return;
                }
                identificador.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                ingredientes.setText(resultado.getString(2));
                preparacion.setText(resultado.getString(3));
                observaciones.setText(resultado.getString(4));

                if(origen==2)
                {
                    agregar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("Confirmar Actualización");
                    identificador.setEnabled(false);
                }
            }
            else
            {
                //no hay
                Toast.makeText(this, "NO HAY DATOS", Toast.LENGTH_LONG).show();
            }
            tabla.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO BUSCAR", Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionEliminar(String datos, final String idABuscar) {
        String [] partes=datos.split("&");
        String mostrardatos= partes[1]+"\n" +partes[2]+"\n" +partes[3]+"\n" +partes[1]+"\n";
        AlertDialog.Builder confirmaEliminacion = new AlertDialog.Builder(this);
        confirmaEliminacion.setTitle("ATENCION !!!")
                .setMessage("Seguro que desesas eliminar los datos de "+idABuscar +"\n" +mostrardatos)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminardato(idABuscar);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar", null).show();
    }

    private void eliminardato(String idABuscar) {
        try {
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "DELETE FROM RECETA WHERE ID=" + idABuscar;
            tabla.execSQL(SQL);
            Toast.makeText(this, "ELIMINADO", Toast.LENGTH_LONG).show();
            tabla.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }

    private void habilitarBotonesyLimpiarCampos() {
        identificador.setText("");
        nombre.setText("");
        preparacion.setText("");
        ingredientes.setText("");
        observaciones.setText("");

        agregar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");

        identificador.setEnabled(true);
    }
    private void actualizarDatos() {
        try
        {
            SQLiteDatabase tabla =base.getWritableDatabase();
            String SQL = "UPDATE RECETA SET NOMBRE='"+nombre.getText().toString()+"', INGREDIENTES='"
                    +ingredientes.getText().toString()+"', PREPARACION='"
                    +preparacion.getText().toString()+"', OBSEVACIONES='"
                    +observaciones.getText().toString()+"' WHERE ID="+identificador.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "SE ACTUALIZO", Toast.LENGTH_LONG).show();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesyLimpiarCampos();
    }
    private void pedirID(final int origen ) {

        final EditText pedirID=new EditText(this);
        pedirID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pedirID.setHint("VALOR ENTERO MAYOR DE 0");
        AlertDialog.Builder alerta =new AlertDialog.Builder(this);
        String mensaje="Escriba el ID a buscar";
        String mensaje2="Buscar";
        if(origen==2)
        {
            mensaje="Escriba el ID a modificar";
            mensaje2="Actualizar";
        }
        if(origen==3)
        {
            mensaje="Escriba el ID a eliminar";
            mensaje2="Eliminar";
        }
        alerta.setTitle("ATENCION!!")
                .setMessage(mensaje)
                .setView(pedirID)
                .setPositiveButton(mensaje2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pedirID.getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity.this, "DEBES INGRESAR UN NUMERO", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pedirID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR", null).show();
    }

    private void codigoAgregar() {
        try
        {
            SQLiteDatabase tabla= base.getWritableDatabase();
            String SQL ="INSERT INTO RECETA VALUES("+identificador.getText().toString()+",'"
                    +nombre.getText().toString()+"','"
                    +ingredientes.getText().toString()+"','"
                    +preparacion.getText().toString()+"','"
                    +observaciones.getText().toString()+"')";

            tabla.execSQL(SQL);
            Toast.makeText(this, "SI SE PUDO INSERTAR", Toast.LENGTH_LONG).show();
            tabla.close();

            identificador.setText("");
            nombre.setText("");
            ingredientes.setText("");
            preparacion.setText("");
            observaciones.setText("");

        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO INSERTAR", Toast.LENGTH_LONG).show();
        }
    }
}
