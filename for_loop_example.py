def sum_to_n(n):
    """Sum integers from 1 to n using a for loop."""
    total = 0
    for i in range(1, n + 1):
        total += i
    return total


def find_even_numbers(n):
    """Return a list of even numbers from 0 to n using a for loop with range."""
    evens = []
    for i in range(0, n + 1, 2):
        evens.append(i)
    return evens


def main():
    print("sum_to_n(10) =", sum_to_n(10))
    print("find_even_numbers(10) =", find_even_numbers(10))


if __name__ == "__main__":
    main()
