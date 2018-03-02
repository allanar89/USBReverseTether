package cu.slam.usbreversetether;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {
	Runtime run;
	Process proc;
	DataOutputStream dos;
	OutputStream os;
	EditText et;
	AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et = (EditText)findViewById(R.id.editText1);
		AddRoot();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				proc.destroy();//mata todos los procesos relacionados con el actual
				this.finish();//finaliza la aplicación				
				return true;
			case R.id.item1:
				AcercaD(getCurrentFocus());
			break;
			case R.id.item2:
				//La donación
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void AddRoot(){
		try {
			/*File f=new File(Environment.getRootDirectory()+"/bin/");
			File xf=new File(Environment.getRootDirectory()+"/xbin/");
			Toast.makeText(getApplicationContext(), f.getAbsolutePath(), Toast.LENGTH_LONG).show();
			proc = new ProcessBuilder()
			.command("netcfg rndis0 up")//4
			.command("setprop net.dns0 8.8.8.8")//3			
			.directory(f)			
			.command("busybox route add default gw 192.168.137.1 rndis0")//2
			.command("busybox ifconfig rndis0 192.168.137.2")//1
			.directory(xf)
			.command("su")//0
			.redirectErrorStream(true)
			.start();*/
		
			run = Runtime.getRuntime();
			proc = run.exec("su");
			/*InputStream is = proc.getInputStream();
			InputStream eis = proc.getErrorStream();
			int i=0, ei=0;
			String lect="",elect="";
			while((i=is.read())!=-1){
				lect+=(char)i;				
			}
			is.close();
			AD(4, lect);*/
			/*while((ei=eis.read())!=-1){
				elect+=(char)ei;				
			}
			eis.close();
			AD(4, elect);*/
		} catch (IOException e) {			
			AD(1, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void ActivarURT(View v){		
		try {		
			//AD(0,"");//muestra msg de inicio de configuración
			
			String dns = et.getText().toString();		
			Process p = Runtime.getRuntime().exec("su root");
			OutputStream os = p.getOutputStream();
			if(dns.isEmpty()){
				//AD(-1,"Se usarán valores por defecto.");
				dns="8.8.4.4";
			}
			if(!dns.isEmpty()){				
				//método primario (poco eficiente)			 	
				os = proc.getOutputStream();
				os.write("netcfg rndis0 up".getBytes());//activar modo USB Tether
				os.write("busybox ifconfig rndis0 192.168.137.2".getBytes());	//establece IP estática,para responder al rango	otorgado por defecto en Windows*/				
				os.write("setprop net.dns0 8.8.8.8".getBytes());//DNS para Google
				os.write(("setprop net.dns1 "+dns).getBytes());//DNS del usuario
				os.write("busybox route add default gw 192.168.137.1 rndis0".getBytes());//establece ruta
				
				
				/*método secundario (por si falla el primero)
				Runtime.getRuntime().exec("busybox ifconfig rndis0 192.168.137.2");
				Runtime.getRuntime().exec("setprop net.dns0 8.8.8.8");
				Runtime.getRuntime().exec("setprop net.dns0 "+dns);
				Runtime.getRuntime().exec("busybox route add default gw 192.168.137.1 rndis0");
				Runtime.getRuntime().exec("netcfg rndis0 up");
				//método mixto*/
				
				
				os.close();//cierra el flujo de información				
				AD(3,"");//muestra msg de activación correcta	
			}
			Button urt = (Button)findViewById(R.id.button1);
			if(urt.getText().toString().contains("Des"))
				urt.setText("Activar U.R.T");					
			else 
				urt.setText("Desactivar U.R.T");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			AD(1,e.getMessage().toString());
			e.printStackTrace();			
		}		
	}
	
	public void AcercaD(View v){
		AD(2,"");
	}

	public void AD(int caso, String msg){
		switch (caso) {
		case 0://comportamiento normal
			dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Información");
			dialog.setMessage("Se procede a configurar el servicio.");
			dialog.show();			
			break;

		case 1://msg de error
			dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Información");
			dialog.setMessage("Error: "+msg);
			dialog.show();
			break;
		case 2://Acerca de
			dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Acerca de USBReverseTether");
			dialog.setMessage("Desarrollado por Slam (4/4/2017)");
			dialog.show();
			break;
		case 3://Conexión establecida
			dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Información");
			dialog.setMessage("El servicio USB Reverse Tether está activo");
			dialog.show();
			break;
		case 4://lectura del stdin
			dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Lectura de stdin");
			dialog.setMessage(msg);
			dialog.show();
			break;
		default://cuando no es una opción permitida
			dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Información");
			dialog.setMessage("Estos datos no son correctos, por favor, verifíquelos.\n"+msg);
			dialog.show();
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
