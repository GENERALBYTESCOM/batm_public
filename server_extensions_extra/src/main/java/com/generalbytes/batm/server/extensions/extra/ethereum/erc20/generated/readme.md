When updating web3j:
- download [web3j cli](https://docs.web3j.io/4.8.7/command_line_tools/)
- regenerate `ERC20Interface.java`:
```
web3j generate solidity --abiFile=server_extensions_extra/src/main/java/com/generalbytes/batm/server/extensions/extra/ethereum/erc20/generated/ERC20Interface.abi --outputDir=server_extensions_extra/src/main/java/ --package=com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated
```
- the generated code depends on some web3j dependencies that are not transitively included, you have to add them to our build.gradle, e.g.
    - io.reactivex.rxjava2:rxjava:2.2.2
    - org.reactivestreams:reactive-streams:1.0.2