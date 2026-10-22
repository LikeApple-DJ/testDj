import pytest
from for_loop_example import sum_to_n, find_even_numbers


def test_sum_to_n_positive():
    assert sum_to_n(5) == 15
    assert sum_to_n(10) == 55
    assert sum_to_n(1) == 1


def test_sum_to_n_zero():
    assert sum_to_n(0) == 0


def test_find_even_numbers():
    assert find_even_numbers(10) == [0, 2, 4, 6, 8, 10]
    assert find_even_numbers(0) == [0]
    assert find_even_numbers(1) == [0]
    assert find_even_numbers(5) == [0, 2, 4]
