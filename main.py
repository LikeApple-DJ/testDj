"""
FastAPI Backend - Demo APIs
Provides: helloworld, hash algorithm, bubble sort, and export endpoints.
"""

import hashlib
import json
import csv
import io
from typing import List, Optional
from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

app = FastAPI(title="Demo APIs", version="1.0.0")

# Enable CORS for frontend access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ──────────────────────────────────────────────
# Request / Response Models
# ──────────────────────────────────────────────

class HashRequest(BaseModel):
    input: str
    algorithm: str = "sha256"  # md5, sha1, sha256, sha512


class HashResponse(BaseModel):
    input: str
    algorithm: str
    result: str


class BubbleSortRequest(BaseModel):
    array: List[int]


class BubbleSortResponse(BaseModel):
    original: List[int]
    result: List[int]
    steps: List[List[int]]
    total_swaps: int


class HelloWorldResponse(BaseModel):
    result: str
    message: str


# ──────────────────────────────────────────────
# 1. Hello World API
# ──────────────────────────────────────────────

@app.get("/api/helloworld", response_model=HelloWorldResponse)
async def helloworld():
    """Return a simple Hello World message."""
    return HelloWorldResponse(
        result="Hello World!",
        message="Welcome to the Demo API. This is the helloworld endpoint."
    )


# ──────────────────────────────────────────────
# 2. Hash Algorithm API
# ──────────────────────────────────────────────

SUPPORTED_ALGORITHMS = ["md5", "sha1", "sha256", "sha512"]


@app.post("/api/hash", response_model=HashResponse)
async def compute_hash(req: HashRequest):
    """Compute hash of input string using the specified algorithm."""
    algorithm = req.algorithm.lower()
    if algorithm not in SUPPORTED_ALGORITHMS:
        raise HTTPException(
            status_code=400,
            detail=f"Unsupported algorithm '{req.algorithm}'. Supported: {SUPPORTED_ALGORITHMS}"
        )

    h = hashlib.new(algorithm)
    h.update(req.input.encode("utf-8"))
    digest = h.hexdigest()

    return HashResponse(
        input=req.input,
        algorithm=algorithm,
        result=digest
    )


# ──────────────────────────────────────────────
# 3. Bubble Sort API
# ──────────────────────────────────────────────

def bubble_sort_with_steps(arr: List[int]):
    """Bubble sort that records each step for visualization."""
    a = arr.copy()
    n = len(a)
    steps = []
    swaps = 0
    steps.append(a.copy())

    for i in range(n):
        swapped = False
        for j in range(0, n - i - 1):
            if a[j] > a[j + 1]:
                a[j], a[j + 1] = a[j + 1], a[j]
                swaps += 1
                swapped = True
                steps.append(a.copy())
        if not swapped:
            break

    return a, steps, swaps


@app.post("/api/bubble-sort", response_model=BubbleSortResponse)
async def bubble_sort(req: BubbleSortRequest):
    """Sort an array using bubble sort and return intermediate steps."""
    if len(req.array) > 1000:
        raise HTTPException(status_code=400, detail="Array too large (max 1000 elements)")

    result, steps, total_swaps = bubble_sort_with_steps(req.array)

    return BubbleSortResponse(
        original=req.array,
        result=result,
        steps=steps,
        total_swaps=total_swaps
    )


# ──────────────────────────────────────────────
# 4. Export API
# ──────────────────────────────────────────────

@app.get("/api/export/{export_type}")
async def export_data(export_type: str):
    """Export data for a given type as CSV file."""
    if export_type == "helloworld":
        # Export helloworld result
        output = io.StringIO()
        writer = csv.writer(output)
        writer.writerow(["Field", "Value"])
        writer.writerow(["Result", "Hello World!"])
        writer.writerow(["Message", "Welcome to the Demo API."])
        content = output.getvalue()
        return StreamingResponse(
            iter([content]),
            media_type="text/csv",
            headers={"Content-Disposition": "attachment; filename=helloworld_export.csv"}
        )

    elif export_type == "hash":
        # Export sample hash results for all algorithms
        sample_input = "Hello World"
        output = io.StringIO()
        writer = csv.writer(output)
        writer.writerow(["Algorithm", "Input", "Hash Result"])
        for algo in SUPPORTED_ALGORITHMS:
            h = hashlib.new(algo)
            h.update(sample_input.encode("utf-8"))
            writer.writerow([algo, sample_input, h.hexdigest()])
        content = output.getvalue()
        return StreamingResponse(
            iter([content]),
            media_type="text/csv",
            headers={"Content-Disposition": "attachment; filename=hash_export.csv"}
        )

    elif export_type == "bubble-sort":
        # Export sample bubble sort result
        sample = [64, 34, 25, 12, 22, 11, 90]
        result, steps, swaps = bubble_sort_with_steps(sample)
        output = io.StringIO()
        writer = csv.writer(output)
        writer.writerow(["Step", "Array State"])
        for i, step in enumerate(steps):
            writer.writerow([i, str(step)])
        writer.writerow([])
        writer.writerow(["Original", str(sample)])
        writer.writerow(["Sorted", str(result)])
        writer.writerow(["Total Swaps", swaps])
        content = output.getvalue()
        return StreamingResponse(
            iter([content]),
            media_type="text/csv",
            headers={"Content-Disposition": "attachment; filename=bubble_sort_export.csv"}
        )

    else:
        raise HTTPException(status_code=400, detail=f"Unknown export type: {export_type}")


# ──────────────────────────────────────────────
# Health check
# ──────────────────────────────────────────────

@app.get("/api/health")
async def health():
    return {"status": "ok"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
