import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.file.R
import java.io.File

interface FileAdapterListener {
    fun onFileLongPressed(file: File)
}

class FileAdapter : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private var files: List<File> = emptyList()
    private var listener: FileAdapterListener? = null

    fun setFiles(files: List<File>) {
        this.files = files
        notifyDataSetChanged()
    }

    fun setListener(listener: FileAdapterListener) {
        this.listener = listener
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        val fileName: TextView = itemView.findViewById(R.id.textFileName)

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View?): Boolean {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onFileLongPressed(files[position])
            }
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.fileName.text = file.name
    }

    override fun getItemCount(): Int {
        return files.size
    }
}
