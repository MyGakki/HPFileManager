package example.com.hpfilemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 12531 on 2017/3/31.
 */

public class FileAdapter extends BaseAdapter {
    ArrayList<File> fileData;
    Context context;
    FileListItemListener fileListItemListener;

    public FileAdapter(Context context,ArrayList<File> fileData) {
        this.fileData = fileData;
        this.context = context;
        fileListItemListener = new FileListItemListener();
    }

    @Override
    public int getCount() {
        return fileData.size();
    }

    @Override
    public Object getItem(int i) {
        return fileData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        File file = fileData.get(i);
        fileListItemListener = new FileListItemListener();
        ViewHolder viewHolder;
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_file_cell,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if(file.isDirectory()) {
            viewHolder.fileImage.setImageResource(R.drawable.folder);
            viewHolder.fileSize.setText("文件夹");
        } else {
            viewHolder.fileImage.setImageResource(R.drawable.file);
            viewHolder.fileSize.setText("文件");
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.fileName.setText(file.getName());
        viewHolder.fileTime.setText(format.format(new Date(file.lastModified())));
        return view;
    }

    public static class ViewHolder {
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
        TextView fileTime;
        public ViewHolder(View view) {
            fileImage = (ImageView) view.findViewById(R.id.file_image);
            fileName = (TextView) view.findViewById(R.id.file_name);
            fileSize = (TextView) view.findViewById(R.id.file_size);
            fileTime = (TextView) view.findViewById(R.id.file_time);
        }
    }

    public class FileListItemListener implements View.OnClickListener {
        Integer position;

        @Override
        public void onClick(View view) {
            position = (Integer) view.getTag();
        }
    }

    public File[] setFileData (ArrayList<File> data) {
        this.fileData = data;
        this.notifyDataSetChanged();
        File[] files = new File[fileData.size()];
        for(int i = 0; i < files.length;i++) {
            files[i] = fileData.get(i);
        }
        return files;
    }
}
