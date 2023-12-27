import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.file.R
import java.io.File

class FolderAdapter : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var folders: List<File> = emptyList()
    private var onItemClick: ((File) -> Unit)? = null

    fun setFolders(folders: List<File>) {
        this.folders = folders
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (File) -> Unit) {
        this.onItemClick = listener
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName: TextView = itemView.findViewById(R.id.textFolderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.folderName.text = folder.name

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(folder)
        }
    }

    override fun getItemCount(): Int {
        return folders.size
    }
}
