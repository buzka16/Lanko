package android.example.tinkoff.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.tinkoff.R
import android.example.tinkoff.databinding.LatestFragmentBinding
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class LatestFragment : Fragment() {

    private lateinit var binding: LatestFragmentBinding
    private lateinit var viewModel: LatestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.latest_fragment, container, false
        )

        viewModel = ViewModelProvider(this)[LatestViewModel::class.java]
        binding.latestViewModel = viewModel

        viewModel.index.observe(
            viewLifecycleOwner
        ) { viewModel.switchBack() }

        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                0 -> {
                    binding.networkB.visibility = View.VISIBLE
                    binding.noNetworkB.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Последние : ошибка загрузки GIF!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                -1 -> {
                    binding.networkB.visibility = View.GONE
                    binding.noNetworkB.visibility = View.VISIBLE
                }
                1 -> {
                    binding.networkB.visibility = View.VISIBLE
                    binding.noNetworkB.visibility = View.GONE
                }
                -2 -> {
                    Toast.makeText(
                        requireContext(),
                        "Последние : вы просмотрели все!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }


        val circularProgressDrawable = CircularProgressDrawable(binding.imageViewB.context).apply {
            strokeWidth = 10f
            centerRadius = 80f
        }
        circularProgressDrawable.start()

        viewModel.image.observe(viewLifecycleOwner) { newImage ->
            Glide.with(requireContext())
                .load(newImage)
                .asGif()
                .placeholder(circularProgressDrawable)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(binding.imageViewB)
        }

        viewModel.getImageOnline()

        binding.lifecycleOwner = this

        return binding.root
    }
}