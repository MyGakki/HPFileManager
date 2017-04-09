package example.com.hpfilemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import static android.R.attr.data;
import static android.R.attr.defaultHeight;
import static android.R.string.no;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.ECLAIR;
import static android.os.Build.VERSION_CODES.M;
import static example.com.hpfilemanager.R.drawable.file;

public class MainActivity extends AppCompatActivity {

    private ListView fileList;
    private String currentPath;
    private TextView textView1;

    private int[] image = {file,R.drawable.folder};
    private File[] files;
    private SimpleAdapter simpleAdapter;
    private ArrayList<File> data = new ArrayList<>();
    private String rootPath;
    private Stack<String> nowPathStack;
    private FileAdapter fileAdapter;

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.new_floder:
//                createNewFolder();
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
//        return true;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[] { Manifest.permission. WRITE_EXTERNAL_STORAGE}, 1);
        }
        initView();
    }

//    public void init(File file) {
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            files = file.listFiles();
//            if(!files.equals(null)) {
//                currentPath = file.getPath();
//                textView1.setText("当前路径为：" + currentPath);
//                List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//                for(int i = 0; i < files.length; i++) {
//                    Map<String, Object> maps = new HashMap<String, Object>();
//                    if(files[i].isFile()) {
//                        maps.put("image",image[0]);
//                    } else {
//                        maps.put("image",image[1]);
//                    }
//                    maps.put("filename",files[i].getName());
//                    list.add(maps);
//                }
//                simpleAdapter = new SimpleAdapter(this,list,R.layout.a_file_layout,
//                        new String[] {"image","filename"},new int[] {R.id.image,R.id.file_folder_name});
//                fileList.setAdapter(simpleAdapter);
//            } else {
//                Toast.makeText(this,"该文件夹为空",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void initView() {
        rootPath = Environment.getExternalStorageDirectory().toString();
        nowPathStack = new Stack<>();
        fileList = (ListView) findViewById(R.id.file_list);
        textView1 = (TextView) findViewById(R.id.current_path_text);
        files = Environment.getExternalStorageDirectory().listFiles();//获取本地文件信息列表
        nowPathStack.push(rootPath);//将路径推入路径栈
        for(File f :files) {
            data.add(f);
        }
        textView1.setText(getPathString());
        fileAdapter = new FileAdapter(this,data);
        fileList.setAdapter(fileAdapter);

        fileList.setOnItemClickListener(new FileItemClickListener());
    }

    class FileItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            File file = files[i];
            if(file.isFile()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri data = Uri.fromFile(file);
                int index = file.getName().lastIndexOf(".");
                String suffix = file.getName().substring(index + 1);
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
                intent.setDataAndType(data,type);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"不支持的文件类型",Toast.LENGTH_SHORT).show();
                }
            } else {
                nowPathStack.push("/" + file.getName());
                showChange(getPathString());
            }
        }
    }

    public void showChange(String path) {
        textView1.setText(path);
        files = new File(path).listFiles();
        data.clear();
        for(File f: files) {
            data.add(f);
        }
        files = fileAdapter.setFileData(data);
    }

    private String getPathString() {
        Stack<String> temp = new Stack<>();
        temp.addAll(nowPathStack);
        String result = "";
        while (temp.size() != 0) {
            result = temp.pop() + result;
        }
        return result;
    }

    long lastBackPressed = 0;
    @Override
    public void onBackPressed() {
        if(nowPathStack.peek() == rootPath) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBackPressed < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            }
            lastBackPressed = currentTime;
        } else {
            nowPathStack.pop();
            showChange(getPathString());
        }
    }

    AlertDialog myDialog;
    EditText newFolderName;

    public void createNewFolder() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                }
        }
    }
}
