package android.example.tinkoff.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.tinkoff.R
import android.example.tinkoff.databinding.LastFragmentBinding
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class LastFragment : Fragment() {

    private lateinit var binding: LastFragmentBinding
    private lateinit var viewModel: LastViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.last_fragment, container, false
        )

        viewModel = ViewModelProvider(this)[LastViewModel::class.java]
        binding.lastViewModel = viewModel

        viewModel.index.observe(
            viewLifecycleOwner
        ) { viewModel.switchBack() }

        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                0 -> {
                    binding.networkA.visibility = View.VISIBLE
                    binding.noNetworkA.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Рандомные : ошибка загрузки GIF!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                -1 -> {
                    binding.networkA.visibility = View.GONE
                    binding.noNetworkA.visibility = View.VISIBLE
                }
                1 -> {
                    binding.networkA.visibility = View.VISIBLE
                    binding.noNetworkA.visibility = View.GONE
                }
            }
        }


        val circularProgressDrawable = CircularProgressDrawable(binding.imageViewA.context).apply {
            strokeWidth = 10f
            centerRadius = 80f
        }
        circularProgressDrawable.start()

        binding.imageViewA.setImageDrawable(circularProgressDrawable)

        viewModel.image.observe(viewLifecycleOwner) { newImage ->
            Glide.with(requireContext())
                .load(newImage)
                .asGif()
                .placeholder(circularProgressDrawable)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(binding.imageViewA)
        }

        viewModel.getImageOnline()

        binding.lifecycleOwner = this

        return binding.root
    }
}