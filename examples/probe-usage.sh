#!/bin/bash

# Example Script: How to use the probe JAR artifacts
# This script demonstrates the usage of encrypted and unencrypted probes

echo "================================================"
echo "Probe JAR Artifacts Usage Examples"
echo "================================================"
echo ""

# Check if JAR files exist
if [ ! -f "target/encrypted-probe.jar" ] || [ ! -f "target/unencrypted-probe.jar" ]; then
    echo "ERROR: JAR files not found. Please build first with:"
    echo "  mvn clean package -DskipTests"
    exit 1
fi

echo "✓ JAR artifacts found:"
echo "  - target/encrypted-probe.jar ($(du -h target/encrypted-probe.jar | cut -f1))"
echo "  - target/unencrypted-probe.jar ($(du -h target/unencrypted-probe.jar | cut -f1))"
echo ""

# Create sample CSV if it doesn't exist
if [ ! -f "/tmp/sample_data.csv" ]; then
    echo "Creating sample CSV file..."
    cat > /tmp/sample_data.csv << 'EOF'
timestamp,sensor_id,temperature,humidity,pressure
2024-01-13 10:00:00,SENSOR001,23.5,45.2,1013.25
2024-01-13 10:01:00,SENSOR002,24.1,46.8,1012.98
2024-01-13 10:02:00,SENSOR003,22.9,44.5,1013.42
2024-01-13 10:03:00,SENSOR004,23.8,45.9,1013.15
2024-01-13 10:04:00,SENSOR005,24.3,47.1,1012.88
EOF
    echo "✓ Sample CSV created at /tmp/sample_data.csv"
    echo ""
fi

echo "================================================"
echo "Example 1: Encrypted Probe (reads 'temperature' column)"
echo "================================================"
echo "Command:"
echo "  java -jar target/encrypted-probe.jar /tmp/sample_data.csv temperature"
echo ""
echo "This will:"
echo "  1. Read the 'temperature' column from the CSV"
echo "  2. Encrypt each value using AES"
echo "  3. Send encrypted events to the monitoring system"
echo ""

echo "================================================"
echo "Example 2: Unencrypted Probe (reads 'humidity' column)"
echo "================================================"
echo "Command:"
echo "  java -jar target/unencrypted-probe.jar /tmp/sample_data.csv humidity"
echo ""
echo "This will:"
echo "  1. Read the 'humidity' column from the CSV"
echo "  2. Send unencrypted events to the monitoring system"
echo ""

echo "================================================"
echo "Example 3: View usage help"
echo "================================================"
echo "Running without arguments shows usage:"
echo ""
java -jar target/encrypted-probe.jar 2>&1 || true
echo ""

echo "================================================"
echo "Notes:"
echo "================================================"
echo "- Both probes require an ActiveMQ broker running at tcp://localhost:61616"
echo "- The CSV file must have a header row with column names"
echo "- Data is sent continuously with random delays (1-40ms) between values"
echo "- The encrypted probe uses AES with key: k3n8JZV5f0sY1y8Q2FzQxQ=="
echo "- See PROBE_ARTIFACTS.md for more details"
echo ""
