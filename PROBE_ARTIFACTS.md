# Probe JAR Artifacts

This project builds two standalone JAR artifacts for monitoring probes:

1. **encrypted-probe.jar** - Encrypts data from CSV files before sending to the monitoring system
2. **unencrypted-probe.jar** - Sends data from CSV files to the monitoring system without encryption

## Building the Artifacts

To build the artifacts, run:

```bash
mvn clean package -DskipTests
```

This will generate the following JAR files in the `target/` directory:
- `encrypted-probe.jar`
- `unencrypted-probe.jar`

## Usage

Both probes accept the same command-line arguments:

```bash
java -jar <probe-jar> <csv_file_path> <column_name>
```

### Parameters

- `csv_file_path`: The path to the CSV file containing the data to send
- `column_name`: The name of the column to read from the CSV file

### Examples

#### Encrypted Probe

```bash
java -jar target/encrypted-probe.jar /path/to/data.csv temperature
```

This will:
1. Read the `temperature` column from `/path/to/data.csv`
2. Encrypt each value using AES encryption
3. Send encrypted events to the monitoring system

#### Unencrypted Probe

```bash
java -jar target/unencrypted-probe.jar /path/to/data.csv humidity
```

This will:
1. Read the `humidity` column from `/path/to/data.csv`
2. Send unencrypted events to the monitoring system

## Encryption

The encrypted probe uses AES encryption with a predefined key. The encryption key is defined in the `Encrypter` class and is the same key used by the monitoring system for decryption.

**Security Note**: The encryption key is currently hardcoded in the source code (`Encrypter.java`). For production use, consider externalizing the key to a configuration file or environment variable.

Default key (Base64): `k3n8JZV5f0sY1y8Q2FzQxQ==`

## Connection Settings

Both probes connect to ActiveMQ broker with the following default settings:
- URL: `tcp://localhost:61616`
- Username: `system`
- Password: `manager`
- Topic: `DROOLS-InstanceOne`

These settings are configured in the probe's main method and can be modified by editing the source code if needed.

## CSV File Format

The CSV file must have:
- A header row with column names
- Data rows with values

Example:
```csv
timestamp,sensor_id,temperature,humidity,status
2024-01-01 10:00:00,SENSOR001,23.5,45.2,OK
2024-01-01 10:01:00,SENSOR002,24.1,46.8,OK
```

## Dependencies

All dependencies are bundled in the JAR files using the Maven Shade plugin, so no additional libraries need to be installed.

## Notes

- The probes continuously read and send data from the CSV file
- Each value is sent as a separate event to the monitoring system
- There is a random delay between 1-40ms between sending each value
- The CSV file is read sequentially from the beginning after reaching the end
