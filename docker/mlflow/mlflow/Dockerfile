FROM python:3.12.13-slim-bookworm

COPY requirements.txt /tmp/requirements.txt
RUN pip install --no-cache-dir -r /tmp/requirements.txt \
    && pip uninstall -y pip setuptools wheel \
    && rm -rf /root/.cache /tmp/requirements.txt
