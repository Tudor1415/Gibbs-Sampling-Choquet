# functions.R
library(kappalab)
library("MultiOrd")
# library(simstudy)
# library(data.table)

#* Get a sample dataset with a given correlation matrix, marginal probabilities and number of number_rows
#* @param correlation_matrix A matrix with the correlations of the items.
#* @param marginal_probabilities The marginal probabilities of each item
#* @param number_rows The number of number_rows
#* @get /sample_dataset
function(correlation_matrix, marginal_probabilities, number_rows) {
  # Parse alternatives from JSON string to R matrix
  marginal_probabilities <- jsonlite::fromJSON(marginal_probabilities)
  correlation_matrix <- jsonlite::fromJSON(correlation_matrix)
  number_rows <- as.numeric(number_rows)

  # Check if the parsed matrix is numeric
  if (!all(sapply(correlation_matrix, is.numeric))) {
    return(list(error = "Input matrix is not well-formed or contains non-numeric values. Please provide a valid numeric matrix."))
  }

  data = generate.binary(number_rows, marginal_probabilities, correlation_matrix)

  # Perform computations and return the result
  result <- list(message = "Dataset generated successfully", Dataset = data)
  return(result)
}


#* Compute the Mobius transformed capacities of the Choquet integral
#* @param alternatives A matrix with the utilities of each alternative.
#* @param overall The overall utilities
#* @param additivity
#* @get /mobius_capacities
function(alternatives, overall, additivity) {
  # Parse alternatives from JSON string to R matrix
  alternatives <- jsonlite::fromJSON(alternatives)
  overall <- jsonlite::fromJSON(overall)
  additivity <- as.numeric(additivity)
  
  # Check if the parsed matrix is numeric
  if (!is.matrix(alternatives) || !all(sapply(alternatives, is.numeric))) {
    return(list(error = "Input matrix is not well-formed or contains non-numeric values. Please provide a valid numeric matrix."))
  }

  nb_items <- ncol(alternatives)
  
  ls <- least.squares.capa.ident(nb_items, additivity, alternatives, overall)
  mobius_capacities <- ls$solution
  values <- slot(mobius_capacities, "data")
  subsets <- slot(mobius_capacities, "subsets")

  # Perform computations and return the result
  result <- list(message = "Mobius capacities computed successfully", Mobius.capacities = values, Mobius.subsets = subsets)
  return(result)
}

#* Evaluate the choquet integral for a given alternative
#* @param alternative A vector for the utilities associated with the alternative
#* @param additivity The aditivity of the choquet integral
#* @param capacities The Mobius transformed capacities of the choquet integral
#* @param subsets The subset associated with each capacity
#* @get /evaluate
function(alternative, additivity, capacities, subsets) {
    alternative <- jsonlite::fromJSON(alternative)
    capacities <- jsonlite::fromJSON(capacities)
    subsets <- jsonlite::fromJSON(subsets)
    additivity <- as.numeric(additivity)

    mobius_capacities <- new("Mobius.capacity",
                            subsets = subsets,
                            k = additivity,
                            data = capacities,
                            n = length(alternative))
    
    score <- Choquet.integral(mobius_capacities, alternative)
    result <- list(message = "Choquet integral computed successfully", score = score)
    return(result)
}
