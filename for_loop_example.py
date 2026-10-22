def sum_to_n(n):
    """Sum integers from 1 to n using a for loop.

    Args:
        n: A non-negative integer.

    Returns:
        The sum of 1..n. Returns 0 when n is 0.

    Raises:
        TypeError: If n is not an integer.
        ValueError: If n is negative.
    """
    if not isinstance(n, int):
        raise TypeError(
            "sum_to_n 仅支持 int 类型，收到: {}".format(type(n).__name__)
        )
    if n < 0:
        raise ValueError("sum_to_n 要求 n >= 0，收到: {}".format(n))
    total = 0
    for i in range(1, n + 1):
        total += i
    return total


def find_even_numbers(n):
    """Return a list of even numbers from 0 to n using a for loop with range.

    Args:
        n: A non-negative integer.

    Returns:
        A list of even integers in [0, n].

    Raises:
        TypeError: If n is not an integer.
        ValueError: If n is negative.
    """
    if not isinstance(n, int):
        raise TypeError(
            "find_even_numbers 仅支持 int 类型，收到: {}".format(type(n).__name__)
        )
    if n < 0:
        raise ValueError("find_even_numbers 要求 n >= 0，收到: {}".format(n))
    evens = []
    for i in range(0, n + 1, 2):
        evens.append(i)
    return evens


def main():
    try:
        print("sum_to_n(10) =", sum_to_n(10))
        print("find_even_numbers(10) =", find_even_numbers(10))
    except (TypeError, ValueError) as e:
        print("Error:", e)


if __name__ == "__main__":
    main()
