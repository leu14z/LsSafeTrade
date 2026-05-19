# LsSafeTrade

O **LsSafeTrade** é um sistema premium e inovador de trocas seguras desenvolvido especificamente para servidores de Minecraft com o mod Pixelmon na versão 1.16.5. O projeto foi projetado com foco em performance otimizada e segurança absoluta, eliminando falhas crônicas de duplicação (dupes), perdas de itens ou problemas com transições de menus.

##  Funcionalidades Principais

* **Interface Premium & Intuitiva:** Menu GUI customizado de 54 slots com divisão visual clara e dinâmica entre as ações de ambos os jogadores.
* **Troca Completa de Pixelmon:** Integração nativa com a API do Pixelmon para seleção e envio de pokémons da equipe ou do PC, exibindo nível, nature, habilidade e IVs detalhados diretamente no Lore do menu.
* **Sistema Anti-Fraude de Economia:** Vinculado diretamente ao Vault. O dinheiro ofertado é retido e debitado no momento exato da oferta via chat, impedindo que jogadores falsifiquem o saldo final durante a confirmação da troca.
* **Proteção Ativa contra Desconexões:** Interceptação imediata de fechamento de inventários involuntários, quedas de conexão ou fechamento forçado do jogo (Alt+F4).
* **Armazenamento de Segurança (Anti-Limbo):** Sistema integrado de `PlayerStorage` baseado em banco de dados local para garantir o estorno seguro de itens, pokémons ou dinheiro caso o inventário do jogador esteja cheio ou ele seja desconectado durante o processo.

## Dependências do Projeto

Para o perfeito funcionamento do ecossistema do plugin, são utilizadas as seguintes dependências em ambiente de produção:

* **LsCore** (Framework base obrigatório para gerenciamento e utilitários da LsPlugins)
* **Vault** (Gerenciamento de transações econômicas)
* **Pixelmon Reforged (1.16.5)** (API e ciclo de objetos de Pokémons)

##  Comandos e Permissões

### Comandos de Jogador
* `/trade <jogador>` — Envia uma solicitação de troca para um jogador próximo.
* `/trade accept` — Aceita uma solicitação de troca pendente.
* `/trade deny` — Recusa uma solicitação de troca pendente.
* `/trade toggle` — Ativa ou desativa o recebimento de novas solicitações de troca.

### Permissões
* `lssafetrade.use` — Garante acesso aos comandos básicos de negociação e utilização das interfaces de troca (Recomendado para o grupo padrão de jogadores).

##  Licença e Distribuição

Este projeto faz parte da linha de plugins comerciais da **LsPlugins**. O código-fonte presente neste repositório serve estritamente como demonstração arquitetural e portfólio de desenvolvimento de sistemas para a plataforma Bukkit/Spigot. Todos os direitos são reservados ao desenvolvedor **leu14z**.
