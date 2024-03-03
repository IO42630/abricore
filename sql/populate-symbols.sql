# fallback if you have deleted the db

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('COMMODITY', 'USD', 'XAUUSD', 'FX:XAUUSD');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('CRYPTO', 'USD', 'BTCUSD', 'COINBASE:BTCUSD');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('STOCK', 'USD', 'AMD', 'NASDAQ:AMD');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('STOCK', 'CHF', 'UBSG', 'SIX:UBSG');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('ETF', 'USD', 'TLT', 'NASDAQ:TLT');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('COMMODITY', 'USD', 'XAGUSD', 'FX:XAGUSD');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('STOCK', 'USD', 'TSLA', 'NASDAQ:TSLA');

INSERT INTO t_symbol (asset_type, currency, name, tw_symbol)
VALUES ('STOCK', 'USD', 'PLTR', 'NYSE:PLTR');

INSERT INTO t_symbol (asset_type, currency, name)
VALUES ('CASH', 'CHF', 'CHF');

INSERT INTO t_symbol (asset_type, currency, name)
VALUES ('CASH', 'USD', 'USD');

INSERT INTO t_symbol (asset_type, currency, name)
VALUES ('CASH', 'EUR', 'EUR');
