package edu.ap.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.ap.myapplication.databinding.ItemResultBinding
import java.util.concurrent.TimeUnit

class ResultsAdapter : ListAdapter<ResultItem, ResultsAdapter.ResultViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResultViewHolder(private val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ResultItem) {
            binding.tvUserName.text = result.userName
            binding.tvScore.text = "Score: ${result.score}"
            binding.tvAddress.text = "Address: ${result.address}"

            // Convert time taken from milliseconds to minutes and seconds
            val minutes = TimeUnit.MILLISECONDS.toMinutes(result.timeTaken)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(result.timeTaken) % 60
            binding.tvTimeTaken.text = "Time Taken: ${minutes}m ${seconds}s"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ResultItem>() {
        override fun areItemsTheSame(oldItem: ResultItem, newItem: ResultItem) = false
        override fun areContentsTheSame(oldItem: ResultItem, newItem: ResultItem) = false
    }
}