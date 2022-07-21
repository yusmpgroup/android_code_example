
class UserActivityFragment @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
) : BaseFragment<FragmentUserActivityBinding, UserActivityState, UserActivityViewModel>() {

    override val viewModel by viewModels<UserActivityViewModel> { viewModelFactory }

    private val activityHistoryAdapter = UserActivityAdapter()

    override val screenStateWatcher: ModelWatcher<UserActivityState> = modelWatcher {
        UserActivityState::userActivityStatistics { userActivityStatistics ->
            if (userActivityStatistics != null) {
                binding.userActivityStatistics.userActivityStatistics = userActivityStatistics.toUi()
                binding.userObjectivesView.userActivities = userActivityStatistics.scores
                binding.userObjectivesView.userActivitiesColors = userActivityStatistics.scores.map { it.color }
                binding.userObjectivesView.isVisible = userActivityStatistics.scores.isNotEmpty()
            }
        }
        UserActivityState::isLoading { isLoading ->
            binding.loadingView.isLoading = isLoading
        }
        UserActivityState::userActivityHistory { activityHistory ->
            activityHistoryAdapter.submitList(activityHistory)
            binding.userActivitiesGroup.isVisible = activityHistory.isNotEmpty()
            binding.noActivityText.isVisible = activityHistory.isEmpty()
        }
        UserActivityState::userActivityHistoryCount { count ->
            binding.activityCountNumberText.text = count.toString()
        }
    }

    override fun inflateFragment(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentUserActivityBinding {
        return FragmentUserActivityBinding.inflate(inflater, container, false)
    }

    override fun initView() = with(binding) {
        setHasOptionsMenu(true)
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.informationMenuItem -> {
                    findNavController().navigate(
                        directions = UserActivityFragmentDirections.toOnboardingFragment()
                    )
                }
            }
            true
        }
        allButton.setOnClickListener {
            findNavController().navigate(
                UserActivityFragmentDirections.toActivityHistoryFragment()
            )
        }
        activityHistoryRecycler.adapter = activityHistoryAdapter
        val scoresViewMaxHeight = getDimension(R.dimen.activity_scores_max_size).toInt()
        val scoresViewMinHeight = getDimension(R.dimen.activity_scores_min_size).toInt()
        AnimationUtils.animateViewSizeOnScroll(
            scrollView = scrollView,
            targetView = userActivityStatistics,
            viewMaxSize = scoresViewMaxHeight,
            viewMinSize = scoresViewMinHeight,
        )
    }
}
