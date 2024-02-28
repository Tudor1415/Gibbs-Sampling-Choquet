# Function to generate all subsets excluding the entire set
all_subsets <- function(n) {
  entire_set <- 1:n
  subsets <- unlist(lapply(1:length(entire_set)-1, function(x) combn(entire_set, x, simplify = FALSE)), recursive = FALSE)

  # Format subsets as strings
  formatted_subsets <- lapply(subsets, function(subset) {
    if (length(subset) == 0) {
      return("{}")
    } else {
      return(paste0("{", paste(subset, collapse = ","), "}"))
    }
  })

  return(formatted_subsets)
}
