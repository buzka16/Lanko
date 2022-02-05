package android.example.tinkoff.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.tinkoff.R
import android.example.tinkoff.databinding.HotFragmentBinding
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class HotFragment : Fragment() {

    private lateinit var binding: HotFragmentBinding
    private lateinit var viewModel: HotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.hot_fragment, container, false
        )

        viewModel = ViewModelProvider(this).get(HotViewModel::class.java)
        binding.hotViewModel = viewModel

        viewModel.index.observe(
            viewLifecycleOwner,
            Observer { viewModel.switchBack() })

        viewModel.networkStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                0 -> {
                    binding.networkB.visibility = View.VISIBLE
                    binding.noNetworkB.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Горячие : ошибка загрузки GIF!",
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
            }
        })


        val circularProgressDrawable = CircularProgressDrawable(binding.imageViewB.context).apply {
            strokeWidth = 10f
            centerRadius = 80f
        }
        circularProgressDrawable.start()

        viewModel.image.observe(viewLifecycleOwner, Observer { newImage ->
            Glide.with(requireContext())
                .load(newImage)
                .asGif()
                .placeholder(circularProgressDrawable)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(binding.imageViewB)
        })

        viewModel.getImageOnline()

        binding.lifecycleOwner = this

        return binding.root
    }
}