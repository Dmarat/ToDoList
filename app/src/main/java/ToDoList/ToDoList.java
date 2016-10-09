package ToDoList;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.course.example.ToDoList.R;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

public class ToDoList extends Activity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener {

    private EditText txtInputField;
    private ArrayList<String> toDoList = new ArrayList<String>();
	private ArrayAdapter<String> adapt = null;
	private ListView listview;
    private OutputStreamWriter out;
    private TextToSpeech speaker;

    public static final String file = "list.txt";

    public int p = 0;

    public String textElement;
    public String textWithoutNum;
    public String listItem;
    public String listItemToDelete;
    public String textUpdateTo;
    public String textUpdate;
    public String path;

    private static final String tag = "To Do List";

    final int save = Menu.FIRST + 1;
    final int close = Menu.FIRST + 2;
    final int add = Menu.FIRST + 3;
    final int delete = Menu.FIRST + 4;
    final int update = Menu.FIRST + 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtInputField = (EditText)findViewById(R.id.txtInput);

		listview = (ListView)findViewById(R.id.list);
		listview.setOnItemClickListener(this);
        
        adapt = new ArrayAdapter<String> (this, R.layout.item, toDoList);
        listview.setAdapter(adapt);

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech (this, this);

        // Creating a path and File Object for the list.txt
        path = getFilesDir().toString() + "/" + file;
        File fileObject = new File(path);

        // Check to see if File Exists
        if(fileObject.exists())
        {
            Log.e(tag, "File Exists.");
            // open stream for reading
            try {
                InputStream fis = openFileInput(file);
                InputStreamReader in = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(in);
                String str = null;

                while ((str = reader.readLine()) != null) {
                    toDoList.add(str);
                }
                reader.close(); // Close stream for reading
            } catch (EOFException e) {
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

    //speaks the contents of output
    public void speak(String output){
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onInit(int status)
    {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // If a language is not be available, the result will indicate it.
            int result = speaker.setLanguage(Locale.US);

            //  int result = speaker.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                // Language data is missing or the language is not supported.
                Log.e(tag, "Language is not available.");
            }
        } else {
            // Initialization failed.
            Log.e(tag, "Could not initialize TextToSpeech.");
        }
    }

    // on destroy
    public void onDestroy(){

        // shut down text to speech engine
        if(speaker != null){
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // create options menu
        super.onCreateOptionsMenu(menu);
        MenuItem item1 = menu.add(0, save, Menu.NONE, "Save List");
        MenuItem item2 = menu.add(0, close, Menu.NONE, "Close App");
        MenuItem item3 = menu.add(0, add, Menu.NONE, "Add Entry");
        MenuItem item4 = menu.add(0, delete, Menu.NONE, "Delete Entry");
        MenuItem item5 = menu.add(0, update, Menu.NONE, "Update Entry");

        return true;
    }

    	//start activity based on list item selected
    	public void onItemClick(AdapterView<?> parent, View v, int position , long id) {

            p = position;
            textElement = toDoList.get(p);
            int posFirstSpace = textElement.indexOf(" ");
            textWithoutNum = textElement.substring(posFirstSpace+1, textElement.length());
            txtInputField.setText(textWithoutNum);

        }
            @Override
            public boolean onOptionsItemSelected (MenuItem item){

                int itemID = item.getItemId();  //get id of menu item picked

                switch (itemID) {

                    case save:
                    {
                        try
                        {
                            out = new OutputStreamWriter(openFileOutput(file, MODE_PRIVATE)); // open output stream
                            for (int i = 0; i < toDoList.size(); i++)
                            {
                                out.write(toDoList.get(i).toString() + " \n"); // write to file
                            }
                            out.close(); // close outpust stream
                        }
                            catch (IOException e)
                            {
                                Log.e("ToDoList ", e.getMessage());
                            }
                        adapt.notifyDataSetChanged();
                        return true;
                    }

                    case close:
                    {
                        try
                        {
                            out = new OutputStreamWriter(openFileOutput(file, MODE_PRIVATE)); // open output stream
                            for (int i = 0; i < toDoList.size(); i++)
                            {
                                out.write(toDoList.get(i).toString() + " \n");
                            }
                            out.close(); // close output stream
                        }
                            catch (IOException e)
                        {
                            Log.e("ToDoList ", e.getMessage());
                        }
                        adapt.notifyDataSetChanged();
                        finish();
                        return true;
                    }

                    case add:
                    {
                        listItem = txtInputField.getText().toString();
                        toDoList.add(toDoList.size()+1 + ". " + listItem);
                        if(speaker.isSpeaking())
                        {
                            Log.i(tag, "Speaker Speaking");
                            speaker.stop();
                            // else start speech
                        } else {
                            Log.i(tag, "Speaker Not Already Speaking");
                            speak(listItem + "Added");
                        }
                        listItem = null;

                        txtInputField.setText("");
                        adapt.notifyDataSetChanged();
                        return true;
                    }
                    case delete:
                    {
                        String str = toDoList.get(p);
                        int posFirstComma = str.indexOf(".");
                        listItemToDelete = str.substring(posFirstComma, str.length());

                        if(speaker.isSpeaking())
                        {
                            Log.i(tag, "Speaker Speaking");
                            speaker.stop();
                            // else start speech
                        } else {
                            Log.i(tag, "Speaker Not Already Speaking");
                            speak(listItemToDelete + " Deleted");
                        }
                        toDoList.remove(p);

                        for (int a = 0; a < toDoList.size(); a++)
                            {
                                String tempItem;
                                tempItem = toDoList.get(a);
                                int posFirstSpace = tempItem.indexOf(" ");
                                String listItemNew = tempItem.substring(posFirstSpace, tempItem.length());
                                toDoList.set(a, a+1 + ". " + listItemNew.trim());
                            }
                        txtInputField.setText("");
                        adapt.notifyDataSetChanged();
                        return true;
                    }
                    case update:
                    {
                        textUpdateTo = txtInputField.getText().toString().trim();
                        int n = p + 1;
                        textUpdate = n + ". " + textUpdateTo;
                        toDoList.set(p, textUpdate);
                        n=0;
                        txtInputField.setText("");
                        adapt.notifyDataSetChanged();
                        return true;
                    }
                    default: super.onOptionsItemSelected(item);
                }
                return false;
            }
}