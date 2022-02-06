package android.example.tinkoff.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.tinkoff.R
import android.example.tinkoff.databinding.BestFragmentBinding
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class BestFragment : Fragment() {

    private lateinit var binding: BestFragmentBinding
    private lateinit var viewModel: BestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.best_fragment, container, false
        )

        viewModel = ViewModelProvider(this)[BestViewModel::class.java]
        binding.bestViewModel = viewModel

        viewModel.index.observe(
            viewLifecycleOwner
        ) { viewModel.switchBack() }

        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                0 -> {
                    binding.networkC.visibility = View.VISIBLE
                    binding.noNetworkC.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Лучшие : ошибка загрузки GIF!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                -1 -> {
                    binding.networkC.visibility = View.GONE
                    binding.noNetworkC.visibility = View.VISIBLE
                }
                1 -> {
                    binding.networkC.visibility = View.VISIBLE
                    binding.noNetworkC.visibility = View.GONE
                }
                -2 -> {
                    Toast.makeText(
                        requireContext(),
                        "Лучшие : вы просмотрели все!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }


        val circularProgressDrawable = CircularProgressDrawable(binding.imageViewC.context).apply {
            strokeWidth = 10f
            centerRadius = 80f
        }
        circularProgressDrawable.start()

        binding.imageViewC.setImageDrawable(circularProgressDrawable)

        viewModel.image.observe(viewLifecycleOwner) { newImage ->
            Glide.with(requireContext())
                .load(newImage)
                .asGif()
                .placeholder(circularProgressDrawable)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(binding.imageViewC)
        }

        viewModel.getImageOnline()

        binding.lifecycleOwner = this

        return binding.root
    }
}