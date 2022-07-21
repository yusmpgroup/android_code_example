
class UserActivityViewModel @Inject constructor(
    private val getUserActivityScoresUseCase: GetUserActivityScoresUseCase,
    private val getUserActivitiesUseCase: GetUserActivitiesUseCase,
) : BaseViewModel<UserActivityState>() {

    init {
        getUserActivities()
    }

    override fun refreshScreen() {
        getUserActivities()
    }

    override fun createInitialState() = UserActivityState(
        isLoading = false,
        userActivityScores = null,
        userActivityHistory = emptyList(),
        userActivityHistoryCount = 0
    )

    private fun getUserActivities() {
        viewModelScope.launch {
            val currentMonth = MonthDateManager().getCurrentMonth()
            val params = GetUserActivitiesUseCaseParams(
                startDate = currentMonth.firstDayDateTime,
                endDate = currentMonth.lastDayDateTime,
            )
            combine(
                getUserActivityScoresUseCase(Unit),
                getUserActivitiesUseCase(params)
            ) { userActivityScores, userActivities ->
                val exception = userActivityScores.exceptionOrNull() ?: userActivities.exceptionOrNull()
                if (exception == null) {
                    Result.success(Data(userActivities.getOrThrow(), userActivityScores.getOrThrow()))
                } else {
                    Result.failure(exception)
                }
            }
                .onStart { updateState { copy(isLoading = true) } }
                .onCompletion { updateState { copy(isLoading = false) } }
                .collect { result ->
                    result.onSuccess { Data ->
                        val activityHistory = Data.userActivities.map { it.toUserActivityHistoryUi() }
                        updateState {
                            copy(
                                userActivityScores = Data.userActivityScores,
                                userActivityHistory = activityHistory,
                                userActivityHistoryCount = activityHistory.size
                            )
                        }
                    }.onFailure { throwable ->
                        showError(throwable)
                    }
                }
        }
    }

    private data class Data(
        val userActivities: List<UserActivityInfo>,
        val userActivityScores: UserActivityScores,
    )
}
