library(kappalab)

# Define the values for the slots
subsets <- c(0, 1, 2, 4, 3, 5, 6, 7, 8, 9, 10)
k_value <- 2
data_values <- c(
    0,
    0.0000010013,
    0.0000010012,
    0.0000010007,
    0.1208,
    1.7333e-10,
    3.9938e-10,
    0.3154,
    2.7138e-10,
    0.2827,
    0.2811
  )
n_value <- 3

# Create a new object of class Mobius.capacity
mobius_capacity <- new("Mobius.capacity",
                            subsets = subsets,
                            k = k_value,
                            data = data_values,
                            n = n_value)

# Check the structure of the new object
str(mobius_capacity)

alternative <- c(0.027, 0.1210762331838565, 0.1377431549304397)

print(Choquet.integral(mobius_capacity, alternative))
